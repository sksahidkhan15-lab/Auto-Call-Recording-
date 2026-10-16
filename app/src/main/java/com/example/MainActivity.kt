package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bridge.WebAppBridge
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AudioPlayerBottomBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PinLockScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WebSyncScreen
import com.example.ui.theme.AutoCallRecorderTheme
import com.example.ui.theme.MainBackground
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAppLocale()
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()
            val currentLanguage by viewModel.appLanguage.collectAsState()

            LaunchedEffect(currentLanguage) {
                applyLocale(currentLanguage)
            }

            AutoCallRecorderTheme {
                MainAppContent(
                    viewModel = viewModel,
                    onShareIntent = { intent -> startActivity(Intent.createChooser(intent, "Share Recording")) },
                    onRestartForLanguage = { lang ->
                        viewModel.setLanguage(lang)
                        applyLocale(lang)
                        recreate()
                    }
                )
            }
        }
    }

    private fun applyAppLocale() {
        val prefs = getSharedPreferences("auto_call_recorder_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("key_language", "en") ?: "en"
        applyLocale(lang)
    }

    private fun applyLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    onShareIntent: (Intent) -> Unit,
    onRestartForLanguage: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isPinAuthenticated by viewModel.isPinAuthenticated.collectAsState()
    val isPinLockEnabled by viewModel.isPinLockEnabled.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Activity Result Launchers
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refreshDiagnostics()
    }

    val importAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importExternalAudio(uri)
            viewModel.navigateTo(AppScreen.LIBRARY)
        }
    }

    // Check permissions on launch if not granted
    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // State collections
    val allRecordings by viewModel.allRecordings.collectAsState()
    val recentRecordings by viewModel.recentRecordings.collectAsState()
    val todayCount by viewModel.todayCount.collectAsState()
    val totalDurationMs by viewModel.totalDurationMs.collectAsState()
    val totalStorageBytes by viewModel.totalStorageBytes.collectAsState()
    val availableStorageBytes by viewModel.availableStorageBytes.collectAsState()
    val isAutoRecordEnabled by viewModel.isAutoRecordEnabled.collectAsState()
    val recordIncoming by viewModel.recordIncoming.collectAsState()
    val recordOutgoing by viewModel.recordOutgoing.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()
    val diagnosticInfo by viewModel.diagnosticInfo.collectAsState()

    // Audio Player State
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val playerDurationMs by viewModel.playerDurationMs.collectAsState()
    val activeRecordingId by viewModel.activeRecordingId.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val activeRecording = remember(activeRecordingId, allRecordings) {
        allRecordings.find { it.id == activeRecordingId }
    }

    // Audio Recorder State
    val recorderState by viewModel.recorderState.collectAsState()
    val recorderDurationMs by viewModel.recorderDurationMs.collectAsState()
    val recorderAmplitude by viewModel.recorderAmplitude.collectAsState()

    // WebAppBridge instance
    val bridge = remember {
        WebAppBridge(
            context = viewModel.getApplication(),
            repository = viewModel.repository,
            playerManager = viewModel.playerManager,
            prefsManager = viewModel.prefsManager,
            onLanguageChanged = { lang -> onRestartForLanguage(lang) }
        )
    }

    // PIN Lock Gate
    if (isPinLockEnabled && !isPinAuthenticated) {
        PinLockScreen(
            onVerifyPin = { pin -> viewModel.verifyPin(pin) }
        )
        return
    }

    // Handle Hardware Back Button
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MainBackground,
        bottomBar = {
            Column {
                if (activeRecording != null) {
                    AudioPlayerBottomBar(
                        activeRecording = activeRecording,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPositionMs,
                        durationMs = playerDurationMs,
                        playbackSpeed = playbackSpeed,
                        onTogglePlay = { viewModel.togglePlayPause(activeRecording) },
                        onSeek = { pos -> viewModel.seekPlayer(pos) },
                        onRewind5s = { viewModel.rewindPlayer5s() },
                        onForward5s = { viewModel.forwardPlayer5s() },
                        onCycleSpeed = { viewModel.cyclePlayerSpeed() },
                        onClose = { viewModel.closePlayer() }
                    )
                }
                AppBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MainBackground)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        recorderState = recorderState,
                        recorderDurationMs = recorderDurationMs,
                        recorderAmplitude = recorderAmplitude,
                        totalCount = allRecordings.size,
                        todayCount = todayCount,
                        totalDurationMs = totalDurationMs,
                        totalStorageBytes = totalStorageBytes,
                        availableStorageBytes = availableStorageBytes,
                        isAutoRecordEnabled = isAutoRecordEnabled,
                        recentRecordings = recentRecordings,
                        activePlayingId = activeRecordingId,
                        isPlaying = isPlaying,
                        onStartMemoRecording = { viewModel.startQuickMemoRecording() },
                        onStopMemoRecording = { viewModel.stopQuickMemoRecording() },
                        onTogglePlayRecording = { rec -> viewModel.togglePlayPause(rec) },
                        onNavigateToLibrary = { viewModel.navigateTo(AppScreen.LIBRARY) },
                        onNavigateToDiagnostics = { viewModel.navigateTo(AppScreen.DIAGNOSTICS) },
                        onNavigateToSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        onPickAudioToImport = { importAudioLauncher.launch("audio/*") }
                    )
                }
                AppScreen.LIBRARY -> {
                    LibraryScreen(
                        recordings = allRecordings,
                        searchQuery = searchQuery,
                        selectedFilter = selectedFilter,
                        selectedSort = selectedSort,
                        activePlayingId = activeRecordingId,
                        isPlaying = isPlaying,
                        onSearchChange = { q -> viewModel.searchQuery.value = q },
                        onFilterSelect = { f -> viewModel.selectedFilter.value = f },
                        onSortSelect = { s -> viewModel.selectedSort.value = s },
                        onTogglePlay = { rec -> viewModel.togglePlayPause(rec) },
                        onToggleStar = { rec -> viewModel.toggleStar(rec) },
                        onRename = { rec, title -> viewModel.renameRecording(rec, title) },
                        onDelete = { rec -> viewModel.deleteRecording(rec) },
                        onShare = { rec ->
                            val intent = viewModel.shareRecording(rec)
                            if (intent != null) onShareIntent(intent)
                        }
                    )
                }
                AppScreen.DIAGNOSTICS -> {
                    DiagnosticsScreen(
                        diagnosticInfo = diagnosticInfo,
                        onRequestPermissions = {
                            val perms = mutableListOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_CONTACTS
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                perms.add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            permissionLauncher.launch(perms.toTypedArray())
                        },
                        onRefresh = { viewModel.refreshDiagnostics() }
                    )
                }
                AppScreen.WEB_SYNC -> {
                    WebSyncScreen(
                        bridge = bridge,
                        onExportJson = {
                            coroutineScope.launch {
                                val json = viewModel.getExportJsonString()
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_SUBJECT, "AutoCallRecorder_Backup.json")
                                    putExtra(Intent.EXTRA_TEXT, json)
                                }
                                onShareIntent(sendIntent)
                            }
                        }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        isAutoRecordEnabled = isAutoRecordEnabled,
                        recordIncoming = recordIncoming,
                        recordOutgoing = recordOutgoing,
                        currentLanguage = appLanguage,
                        isPinEnabled = isPinLockEnabled,
                        onToggleAutoRecord = { viewModel.toggleAutoRecord(it) },
                        onToggleRecordIncoming = { viewModel.toggleRecordIncoming(it) },
                        onToggleRecordOutgoing = { viewModel.toggleRecordOutgoing(it) },
                        onLanguageSelected = { lang -> onRestartForLanguage(lang) },
                        onSetPin = { pin -> viewModel.setPinCode(pin) },
                        onDisablePin = { viewModel.disablePin() }
                    )
                }
            }
        }
    }
}
