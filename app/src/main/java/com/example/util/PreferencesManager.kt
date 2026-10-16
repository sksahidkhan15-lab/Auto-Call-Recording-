package com.example.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auto_call_recorder_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTO_RECORD = "key_auto_record"
        private const val KEY_RECORD_INCOMING = "key_record_incoming"
        private const val KEY_RECORD_OUTGOING = "key_record_outgoing"
        private const val KEY_AUDIO_SOURCE = "key_audio_source"
        private const val KEY_AUDIO_FORMAT = "key_audio_format"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_PIN_CODE = "key_pin_code"
    }

    var isAutoRecordEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RECORD, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_RECORD, value).apply()

    var recordIncoming: Boolean
        get() = prefs.getBoolean(KEY_RECORD_INCOMING, true)
        set(value) = prefs.edit().putBoolean(KEY_RECORD_INCOMING, value).apply()

    var recordOutgoing: Boolean
        get() = prefs.getBoolean(KEY_RECORD_OUTGOING, true)
        set(value) = prefs.edit().putBoolean(KEY_RECORD_OUTGOING, value).apply()

    var audioSource: String
        get() = prefs.getString(KEY_AUDIO_SOURCE, "VOICE_COMMUNICATION") ?: "VOICE_COMMUNICATION"
        set(value) = prefs.edit().putString(KEY_AUDIO_SOURCE, value).apply()

    var audioFormat: String
        get() = prefs.getString(KEY_AUDIO_FORMAT, "M4A") ?: "M4A"
        set(value) = prefs.edit().putString(KEY_AUDIO_FORMAT, value).apply()

    var appLanguage: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var isPinEnabled: Boolean
        get() = prefs.getBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var pinCode: String
        get() = prefs.getString(KEY_PIN_CODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()
}
