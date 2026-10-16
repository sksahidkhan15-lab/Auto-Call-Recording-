package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.audio.AudioRecorderManager
import com.example.data.local.AppDatabase
import com.example.data.model.RecordingEntity
import com.example.data.repository.RecordingRepository
import com.example.util.ContactHelper
import com.example.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallRecordService : Service() {

    private val tag = "CallRecordService"
    private lateinit var recorderManager: AudioRecorderManager
    private lateinit var repository: RecordingRepository
    private lateinit var prefsManager: PreferencesManager
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private var currentCallType: String = "INCOMING"
    private var currentPhoneNumber: String? = null

    companion object {
        const val CHANNEL_ID = "call_recording_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_RECORDING = "com.example.service.ACTION_START"
        const val ACTION_STOP_RECORDING = "com.example.service.ACTION_STOP"
        const val EXTRA_CALL_TYPE = "extra_call_type"
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"

        fun start(context: Context, callType: String, phoneNumber: String?) {
            val intent = Intent(context, CallRecordService::class.java).apply {
                action = ACTION_START_RECORDING
                putExtra(EXTRA_CALL_TYPE, callType)
                putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, CallRecordService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        recorderManager = AudioRecorderManager(this)
        val dao = AppDatabase.getDatabase(this).recordingDao()
        repository = RecordingRepository(dao)
        prefsManager = PreferencesManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                currentCallType = intent.getStringExtra(EXTRA_CALL_TYPE) ?: "INCOMING"
                currentPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)

                val notification = buildForegroundNotification(currentCallType, currentPhoneNumber)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceCompat.startForeground(
                        this,
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                val source = prefsManager.audioSource
                recorderManager.startRecording(
                    callType = currentCallType,
                    phoneNumber = currentPhoneNumber,
                    preferredSource = source
                )
            }
            ACTION_STOP_RECORDING -> {
                handleStopRecording()
            }
        }
        return START_NOT_STICKY
    }

    private fun handleStopRecording() {
        val result = recorderManager.stopRecording()
        if (result != null && result.file.exists() && result.file.length() > 0) {
            serviceScope.launch {
                val resolvedContact = ContactHelper.resolveContactName(this@CallRecordService, currentPhoneNumber)
                val displayTitle = resolvedContact
                    ?: currentPhoneNumber
                    ?: ("Call Record " + System.currentTimeMillis())

                val entity = RecordingEntity(
                    title = displayTitle,
                    filePath = result.file.absolutePath,
                    phoneNumber = currentPhoneNumber,
                    contactName = resolvedContact,
                    callType = currentCallType,
                    timestamp = System.currentTimeMillis(),
                    durationMs = result.durationMs,
                    fileSizeBytes = result.fileSizeBytes,
                    format = "m4a",
                    isStarred = false,
                    note = "Auto-recorded via $currentCallType call"
                )
                repository.insertRecording(entity)
                Log.d(tag, "Successfully saved call recording: ${result.file.name}")
            }
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Call Recording Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active recording status indicator"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(callType: String, phoneNumber: String?): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingOpenApp = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, CallRecordService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        val pendingStop = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val subtitle = if (phoneNumber.isNullOrBlank()) {
            "Recording active call..."
        } else {
            "Recording $callType call with $phoneNumber"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Auto Call Recorder Active")
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingOpenApp)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "Stop Recording", pendingStop)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        recorderManager.stopRecording()
        super.onDestroy()
    }
}
