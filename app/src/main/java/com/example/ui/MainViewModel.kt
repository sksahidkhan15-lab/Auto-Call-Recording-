package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.AudioRecorderManager
import com.example.audio.RecorderState
import com.example.data.local.AppDatabase
import com.example.data.model.RecordingEntity
import com.example.data.repository.RecordingRepository
import com.example.util.DeviceDiagnosticInfo
import com.example.util.DiagnosticsHelper
import com.example.util.PreferencesManager
import com.example.util.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class AppScreen {
    DASHBOARD,
    LIBRARY,
    DIAGNOSTICS,
    WEB_SYNC,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    val repository: RecordingRepository
    val playerManager: AudioPlayerManager
    val recorderManager: AudioRecorderManager
    val prefsManager: PreferencesManager

    init {
        val dao = AppDatabase.getDatabase(application).recordingDao()
        repository = RecordingRepository(dao)
        playerManager = AudioPlayerManager(application)
        recorderManager = AudioRecorderManager(application)
        prefsManager = PreferencesManager(application)
    }

    // Navigation & Security
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isPinAuthenticated = MutableStateFlow(!prefsManager.isPinEnabled)
    val isPinAuthenticated: StateFlow<Boolean> = _isPinAuthenticated.asStateFlow()

    // Settings state
    private val _appLanguage = MutableStateFlow(prefsManager.appLanguage)
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _isAutoRecordEnabled = MutableStateFlow(prefsManager.isAutoRecordEnabled)
    val isAutoRecordEnabled: StateFlow<Boolean> = _isAutoRecordEnabled.asStateFlow()

    private val _recordIncoming = MutableStateFlow(prefsManager.recordIncoming)
    val recordIncoming: StateFlow<Boolean> = _recordIncoming.asStateFlow()

    private val _recordOutgoing = MutableStateFlow(prefsManager.recordOutgoing)
    val recordOutgoing: StateFlow<Boolean> = _recordOutgoing.asStateFlow()

    private val _isPinLockEnabled = MutableStateFlow(prefsManager.isPinEnabled)
    val isPinLockEnabled: StateFlow<Boolean> = _isPinLockEnabled.asStateFlow()

    // Filter & Search
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("ALL") // ALL, INCOMING, OUTGOING, MEMO, IMPORTED, STARRED
    val selectedSort = MutableStateFlow("NEWEST") // NEWEST, OLDEST, DURATION, SIZE

    // Reactive Recordings with Filter and Search
    val allRecordings: StateFlow<List<RecordingEntity>> = combine(
        repository.allRecordings,
        searchQuery,
        selectedFilter,
        selectedSort
    ) { list, query, filter, sort ->
        var result = list

        // Apply filter
        if (filter == "STARRED") {
            result = result.filter { it.isStarred }
        } else if (filter != "ALL") {
            result = result.filter { it.callType.equals(filter, ignoreCase = true) }
        }

        // Apply search query
        if (query.isNotBlank()) {
            val q = query.lowercase().trim()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                (it.phoneNumber?.lowercase()?.contains(q) == true) ||
                (it.contactName?.lowercase()?.contains(q) == true) ||
                (it.note?.lowercase()?.contains(q) == true)
            }
        }

        // Apply sort
        when (sort) {
            "NEWEST" -> result.sortedByDescending { it.timestamp }
            "OLDEST" -> result.sortedBy { it.timestamp }
            "DURATION" -> result.sortedByDescending { it.durationMs }
            "SIZE" -> result.sortedByDescending { it.fileSizeBytes }
            else -> result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentRecordings: StateFlow<List<RecordingEntity>> = repository.recentRecordings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCount: StateFlow<Int> = repository.getTodayCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDurationMs: StateFlow<Long> = repository.totalDuration
        .combine(MutableStateFlow(0L)) { dur, _ -> dur ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalStorageBytes: StateFlow<Long> = repository.totalSize
        .combine(MutableStateFlow(0L)) { size, _ -> size ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _availableStorageBytes = MutableStateFlow(StorageHelper.getAvailableStorageBytes(application))
    val availableStorageBytes: StateFlow<Long> = _availableStorageBytes.asStateFlow()

    // Diagnostics
    private val _diagnosticInfo = MutableStateFlow(DiagnosticsHelper.getDiagnosticInfo(application))
    val diagnosticInfo: StateFlow<DeviceDiagnosticInfo> = _diagnosticInfo.asStateFlow()

    // Audio Player State
    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPositionMs: StateFlow<Long> = playerManager.currentPositionMs
    val playerDurationMs: StateFlow<Long> = playerManager.durationMs
    val activeRecordingId: StateFlow<Long?> = playerManager.activeRecordingId
    val playbackSpeed: StateFlow<Float> = playerManager.playbackSpeed

    // Audio Recorder State
    val recorderState: StateFlow<RecorderState> = recorderManager.state
    val recorderDurationMs: StateFlow<Long> = recorderManager.currentDurationMs
    val recorderAmplitude: StateFlow<Int> = recorderManager.maxAmplitude

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun refreshDiagnostics() {
        _diagnosticInfo.value = DiagnosticsHelper.getDiagnosticInfo(context)
        _availableStorageBytes.value = StorageHelper.getAvailableStorageBytes(context)
    }

    fun setLanguage(lang: String) {
        prefsManager.appLanguage = lang
        _appLanguage.value = lang
    }

    fun toggleAutoRecord(enabled: Boolean) {
        prefsManager.isAutoRecordEnabled = enabled
        _isAutoRecordEnabled.value = enabled
    }

    fun toggleRecordIncoming(enabled: Boolean) {
        prefsManager.recordIncoming = enabled
        _recordIncoming.value = enabled
    }

    fun toggleRecordOutgoing(enabled: Boolean) {
        prefsManager.recordOutgoing = enabled
        _recordOutgoing.value = enabled
    }

    fun setPinCode(pin: String) {
        prefsManager.pinCode = pin
        prefsManager.isPinEnabled = pin.length == 4
        _isPinLockEnabled.value = prefsManager.isPinEnabled
        _isPinAuthenticated.value = true
    }

    fun disablePin() {
        prefsManager.pinCode = ""
        prefsManager.isPinEnabled = false
        _isPinLockEnabled.value = false
        _isPinAuthenticated.value = true
    }

    fun verifyPin(input: String): Boolean {
        return if (prefsManager.pinCode == input) {
            _isPinAuthenticated.value = true
            true
        } else {
            false
        }
    }

    // Player Actions
    fun togglePlayPause(recording: RecordingEntity) {
        playerManager.togglePlayPause(recording.id, recording.filePath)
    }

    fun seekPlayer(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun rewindPlayer5s() {
        playerManager.rewind5s()
    }

    fun forwardPlayer5s() {
        playerManager.forward5s()
    }

    fun cyclePlayerSpeed() {
        playerManager.cycleSpeed()
    }

    fun closePlayer() {
        playerManager.stop()
    }

    // Recording Actions
    fun startQuickMemoRecording() {
        recorderManager.startRecording(
            callType = "MEMO",
            phoneNumber = null,
            preferredSource = prefsManager.audioSource
        )
    }

    fun stopQuickMemoRecording(customTitle: String? = null, phoneNumber: String? = null) {
        val result = recorderManager.stopRecording()
        if (result != null && result.file.exists() && result.file.length() > 0) {
            viewModelScope.launch {
                val title = customTitle?.takeIf { it.isNotBlank() }
                    ?: ("Voice Memo " + StorageHelper.formatDuration(result.durationMs))

                val entity = RecordingEntity(
                    title = title,
                    filePath = result.file.absolutePath,
                    phoneNumber = phoneNumber,
                    contactName = null,
                    callType = "MEMO",
                    timestamp = System.currentTimeMillis(),
                    durationMs = result.durationMs,
                    fileSizeBytes = result.fileSizeBytes,
                    format = "m4a",
                    isStarred = false,
                    note = "Manual voice recording"
                )
                repository.insertRecording(entity)
                _availableStorageBytes.value = StorageHelper.getAvailableStorageBytes(context)
            }
        }
    }

    fun toggleStar(recording: RecordingEntity) {
        viewModelScope.launch {
            repository.updateRecording(recording.copy(isStarred = !recording.isStarred))
        }
    }

    fun renameRecording(recording: RecordingEntity, newTitle: String) {
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                repository.updateRecording(recording.copy(title = newTitle.trim()))
            }
        }
    }

    fun deleteRecording(recording: RecordingEntity) {
        viewModelScope.launch {
            if (activeRecordingId.value == recording.id) {
                playerManager.stop()
            }
            repository.deleteRecording(recording)
            _availableStorageBytes.value = StorageHelper.getAvailableStorageBytes(context)
        }
    }

    fun shareRecording(recording: RecordingEntity): Intent? {
        val file = File(recording.filePath)
        if (!file.exists()) return null

        val uri = StorageHelper.getContentUriForFile(context, file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, recording.title)
            putExtra(Intent.EXTRA_TEXT, "Recording: ${recording.title} (${StorageHelper.formatDuration(recording.durationMs)})")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun importExternalAudio(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val dir = StorageHelper.getRecordingsDirectory(context)
                val timeStamp = System.currentTimeMillis()
                val targetFile = File(dir, "ACR_IMPORTED_$timeStamp.m4a")

                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                if (targetFile.exists() && targetFile.length() > 0) {
                    var durMs = 0L
                    try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(targetFile.absolutePath)
                        val durStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        durMs = durStr?.toLongOrNull() ?: 0L
                        retriever.release()
                    } catch (_: Exception) {}

                    val entity = RecordingEntity(
                        title = "Imported Audio " + StorageHelper.formatBytes(targetFile.length()),
                        filePath = targetFile.absolutePath,
                        phoneNumber = null,
                        contactName = null,
                        callType = "IMPORTED",
                        timestamp = timeStamp,
                        durationMs = durMs,
                        fileSizeBytes = targetFile.length(),
                        format = targetFile.extension.ifBlank { "m4a" },
                        isStarred = false,
                        note = "Imported from external storage"
                    )
                    repository.insertRecording(entity)
                    _availableStorageBytes.value = StorageHelper.getAvailableStorageBytes(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getExportJsonString(): String {
        return withContext(Dispatchers.IO) {
            repository.exportMetadataJson(allRecordings.value)
        }
    }

    override fun onCleared() {
        playerManager.stop()
        recorderManager.stopRecording()
        super.onCleared()
    }
}
