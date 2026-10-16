package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.service.CallRecordService
import com.example.util.PreferencesManager

class CallBroadcastReceiver : BroadcastReceiver() {

    private val tag = "CallBroadcastReceiver"

    companion object {
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var isIncoming = false
        private var savedNumber: String? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)
        if (!prefs.isAutoRecordEnabled) {
            return
        }

        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            savedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            isIncoming = false
            return
        }

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            if (!number.isNullOrBlank()) {
                savedNumber = number
            }

            var state = TelephonyManager.CALL_STATE_IDLE
            when (stateStr) {
                TelephonyManager.EXTRA_STATE_RINGING -> state = TelephonyManager.CALL_STATE_RINGING
                TelephonyManager.EXTRA_STATE_OFFHOOK -> state = TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_IDLE -> state = TelephonyManager.CALL_STATE_IDLE
            }

            onCustomCallStateChanged(context, prefs, state, savedNumber)
        }
    }

    private fun onCustomCallStateChanged(
        context: Context,
        prefs: PreferencesManager,
        state: Int,
        number: String?
    ) {
        if (lastState == state) {
            return
        }

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                Log.d(tag, "Incoming call ringing from: $number")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                // Call answered or outgoing call placed
                if (isIncoming) {
                    if (prefs.recordIncoming) {
                        Log.d(tag, "Starting recording for incoming call...")
                        CallRecordService.start(context, "INCOMING", number)
                    }
                } else {
                    if (prefs.recordOutgoing) {
                        Log.d(tag, "Starting recording for outgoing call...")
                        CallRecordService.start(context, "OUTGOING", number)
                    }
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                // Call ended
                if (lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Log.d(tag, "Call ended, stopping CallRecordService...")
                    CallRecordService.stop(context)
                }
                isIncoming = false
                savedNumber = null
            }
        }
        lastState = state
    }
}
