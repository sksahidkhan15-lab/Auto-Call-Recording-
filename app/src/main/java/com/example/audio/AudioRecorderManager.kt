package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.example.util.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class RecorderState {
    IDLE,
    RECORDING,
    PAUSED
}

data class RecordingSessionResult(
    val file: File,
    val durationMs: Long,
    val fileSizeBytes: Long
)

class AudioRecorderManager(private val context: Context) {

    private val tag = "AudioRecorderManager"
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var startTimeMs: Long = 0L
    private var totalRecordedMs: Long = 0L

    private val _state = MutableStateFlow(RecorderState.IDLE)
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    private val _currentDurationMs = MutableStateFlow(0L)
    val currentDurationMs: StateFlow<Long> = _currentDurationMs.asStateFlow()

    private val _maxAmplitude = MutableStateFlow(0)
    val maxAmplitude: StateFlow<Int> = _maxAmplitude.asStateFlow()

    private var pollingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun startRecording(callType: String = "MEMO", phoneNumber: String? = null, preferredSource: String = "VOICE_COMMUNICATION"): Boolean {
        if (_state.value != RecorderState.IDLE) {
            stopRecording()
        }

        try {
            val outputFile = StorageHelper.createAudioOutputFile(context, callType, phoneNumber)
            currentOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            // Determine audio source
            val audioSource = when (preferredSource) {
                "MIC" -> MediaRecorder.AudioSource.MIC
                else -> MediaRecorder.AudioSource.VOICE_COMMUNICATION
            }

            recorder.setAudioSource(audioSource)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(128000)
            recorder.setAudioSamplingRate(44100)
            recorder.setOutputFile(outputFile.absolutePath)

            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            startTimeMs = System.currentTimeMillis()
            totalRecordedMs = 0L
            _state.value = RecorderState.RECORDING

            startPolling()
            return true
        } catch (e: Exception) {
            Log.e(tag, "Failed to start recording with $preferredSource, trying MIC fallback", e)
            return tryFallbackMic(callType, phoneNumber)
        }
    }

    private fun tryFallbackMic(callType: String, phoneNumber: String?): Boolean {
        return try {
            val outputFile = StorageHelper.createAudioOutputFile(context, callType, phoneNumber)
            currentOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(96000)
            recorder.setAudioSamplingRate(44100)
            recorder.setOutputFile(outputFile.absolutePath)

            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            startTimeMs = System.currentTimeMillis()
            totalRecordedMs = 0L
            _state.value = RecorderState.RECORDING

            startPolling()
            true
        } catch (ex: Exception) {
            Log.e(tag, "Fallback MIC recording also failed", ex)
            _state.value = RecorderState.IDLE
            false
        }
    }

    fun pauseRecording() {
        if (_state.value == RecorderState.RECORDING && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.pause()
                totalRecordedMs += (System.currentTimeMillis() - startTimeMs)
                _state.value = RecorderState.PAUSED
            } catch (e: Exception) {
                Log.e(tag, "Error pausing recorder", e)
            }
        }
    }

    fun resumeRecording() {
        if (_state.value == RecorderState.PAUSED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.resume()
                startTimeMs = System.currentTimeMillis()
                _state.value = RecorderState.RECORDING
            } catch (e: Exception) {
                Log.e(tag, "Error resuming recorder", e)
            }
        }
    }

    fun stopRecording(): RecordingSessionResult? {
        stopPolling()
        val recorder = mediaRecorder ?: return null

        return try {
            recorder.stop()
            recorder.reset()
            recorder.release()
            mediaRecorder = null

            val finalDuration = if (_state.value == RecorderState.RECORDING) {
                totalRecordedMs + (System.currentTimeMillis() - startTimeMs)
            } else {
                totalRecordedMs
            }

            _state.value = RecorderState.IDLE
            _currentDurationMs.value = 0L
            _maxAmplitude.value = 0

            val file = currentOutputFile
            if (file != null && file.exists() && file.length() > 0) {
                RecordingSessionResult(
                    file = file,
                    durationMs = finalDuration,
                    fileSizeBytes = file.length()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping recorder", e)
            try {
                recorder.release()
            } catch (_: Exception) {}
            mediaRecorder = null
            _state.value = RecorderState.IDLE
            null
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive && _state.value == RecorderState.RECORDING) {
                val elapsed = totalRecordedMs + (System.currentTimeMillis() - startTimeMs)
                _currentDurationMs.value = elapsed

                try {
                    val amp = mediaRecorder?.maxAmplitude ?: 0
                    _maxAmplitude.value = amp
                } catch (_: Exception) {}

                delay(100)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }
}
