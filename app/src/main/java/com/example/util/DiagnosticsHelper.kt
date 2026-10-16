package com.example.util

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat

data class DeviceDiagnosticInfo(
    val androidVersion: String,
    val apiLevel: Int,
    val manufacturer: String,
    val model: String,
    val isMicPermissionGranted: Boolean,
    val isPhoneStatePermissionGranted: Boolean,
    val isContactsPermissionGranted: Boolean,
    val isNotificationPermissionGranted: Boolean,
    val isVoiceCommSupported: Boolean,
    val isMicSupported: Boolean
)

object DiagnosticsHelper {

    fun getDiagnosticInfo(context: Context): DeviceDiagnosticInfo {
        val micGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val phoneStateGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        val contactsGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val voiceCommSupported = testAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        val micSupported = testAudioSource(MediaRecorder.AudioSource.MIC)

        return DeviceDiagnosticInfo(
            androidVersion = Build.VERSION.RELEASE ?: "Unknown",
            apiLevel = Build.VERSION.SDK_INT,
            manufacturer = Build.MANUFACTURER?.replaceFirstChar { it.uppercase() } ?: "Unknown",
            model = Build.MODEL ?: "Unknown",
            isMicPermissionGranted = micGranted,
            isPhoneStatePermissionGranted = phoneStateGranted,
            isContactsPermissionGranted = contactsGranted,
            isNotificationPermissionGranted = notifGranted,
            isVoiceCommSupported = voiceCommSupported,
            isMicSupported = micSupported
        )
    }

    private fun testAudioSource(source: Int): Boolean {
        return try {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            if (bufferSize > 0) {
                // If buffer calculation succeeds, the hardware audio HAL supports this profile
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
