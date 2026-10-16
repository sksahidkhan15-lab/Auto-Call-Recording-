package com.example.bridge

import android.content.Context
import android.os.Build
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.audio.AudioPlayerManager
import com.example.data.repository.RecordingRepository
import com.example.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class WebAppBridge(
    private val context: Context,
    private val repository: RecordingRepository,
    private val playerManager: AudioPlayerManager,
    private val prefsManager: PreferencesManager,
    private val onLanguageChanged: (String) -> Unit
) {

    @JavascriptInterface
    fun getDeviceInfo(): String {
        val obj = JSONObject().apply {
            put("release", Build.VERSION.RELEASE ?: "Unknown")
            put("sdkInt", Build.VERSION.SDK_INT)
            put("manufacturer", Build.MANUFACTURER ?: "Unknown")
            put("model", Build.MODEL ?: "Unknown")
            put("isAutoRecordEnabled", prefsManager.isAutoRecordEnabled)
        }
        return obj.toString()
    }

    @JavascriptInterface
    fun getRecordingsJson(): String {
        return runBlocking(Dispatchers.IO) {
            val list = repository.allRecordings.first()
            repository.exportMetadataJson(list)
        }
    }

    @JavascriptInterface
    fun setAppLanguage(lang: String) {
        if (lang == "en" || lang == "bn") {
            prefsManager.appLanguage = lang
            CoroutineScope(Dispatchers.Main).launch {
                onLanguageChanged(lang)
            }
        }
    }

    @JavascriptInterface
    fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun playNativeAudio(filePath: String) {
        CoroutineScope(Dispatchers.Main).launch {
            playerManager.play(0L, filePath)
        }
    }
}
