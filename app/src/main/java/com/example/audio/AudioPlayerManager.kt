package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
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

class AudioPlayerManager(private val context: Context) {

    private val tag = "AudioPlayerManager"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _activeRecordingId = MutableStateFlow<Long?>(null)
    val activeRecordingId: StateFlow<Long?> = _activeRecordingId.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    fun play(recordingId: Long, filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(tag, "Audio file does not exist: $filePath")
            return false
        }

        // If same file already loaded and paused, resume
        if (_activeRecordingId.value == recordingId && mediaPlayer != null) {
            resume()
            return true
        }

        stop()

        try {
            val player = MediaPlayer()
            player.setDataSource(filePath)
            player.prepare()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                player.playbackParams = player.playbackParams.setSpeed(_playbackSpeed.value)
            }

            player.setOnCompletionListener {
                _isPlaying.value = false
                _currentPositionMs.value = 0L
                stopProgressJob()
            }

            player.start()
            mediaPlayer = player
            _activeRecordingId.value = recordingId
            _isPlaying.value = true
            _durationMs.value = player.duration.toLong().coerceAtLeast(0L)

            startProgressJob()
            return true
        } catch (e: Exception) {
            Log.e(tag, "Failed to play audio", e)
            stop()
            return false
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
            stopProgressJob()
        } catch (e: Exception) {
            Log.e(tag, "Failed to pause", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressJob()
        } catch (e: Exception) {
            Log.e(tag, "Failed to resume", e)
        }
    }

    fun togglePlayPause(recordingId: Long, filePath: String) {
        if (_activeRecordingId.value == recordingId) {
            if (_isPlaying.value) pause() else resume()
        } else {
            play(recordingId, filePath)
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
            _currentPositionMs.value = positionMs
        } catch (e: Exception) {
            Log.e(tag, "Failed to seek", e)
        }
    }

    fun rewind5s() {
        val newPos = (_currentPositionMs.value - 5000L).coerceAtLeast(0L)
        seekTo(newPos)
    }

    fun forward5s() {
        val newPos = (_currentPositionMs.value + 5000L).coerceAtMost(_durationMs.value)
        seekTo(newPos)
    }

    fun cycleSpeed(): Float {
        val nextSpeed = when (_playbackSpeed.value) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 2.0f
            else -> 1.0f
        }
        setPlaybackSpeed(nextSpeed)
        return nextSpeed
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null && _isPlaying.value) {
                mediaPlayer?.playbackParams = mediaPlayer!!.playbackParams.setSpeed(speed)
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to set playback speed", e)
        }
    }

    fun stop() {
        stopProgressJob()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionMs.value = 0L
        _durationMs.value = 0L
        _activeRecordingId.value = null
    }

    private fun startProgressJob() {
        stopProgressJob()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                try {
                    val pos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    _currentPositionMs.value = pos
                } catch (_: Exception) {}
                delay(100)
            }
        }
    }

    private fun stopProgressJob() {
        progressJob?.cancel()
        progressJob = null
    }
}
