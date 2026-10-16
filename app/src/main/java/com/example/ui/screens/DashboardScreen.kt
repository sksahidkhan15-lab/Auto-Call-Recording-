package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.RecorderState
import com.example.data.model.RecordingEntity
import com.example.ui.AppScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ContainerHigh
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.WarningColor
import com.example.util.StorageHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    recorderState: RecorderState,
    recorderDurationMs: Long,
    recorderAmplitude: Int,
    totalCount: Int,
    todayCount: Int,
    totalDurationMs: Long,
    totalStorageBytes: Long,
    availableStorageBytes: Long,
    isAutoRecordEnabled: Boolean,
    recentRecordings: List<RecordingEntity>,
    activePlayingId: Long?,
    isPlaying: Boolean,
    onStartMemoRecording: () -> Unit,
    onStopMemoRecording: () -> Unit,
    onTogglePlayRecording: (RecordingEntity) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPickAudioToImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // App Header Bar with Settings shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ContainerHigh)
                            .border(1.dp, PrimaryAccent, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Logo",
                            tint = PrimaryAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MainText
                        )
                        Text(
                            text = stringResource(id = R.string.dashboard_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("btn_dashboard_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = SecondaryText
                    )
                }
            }
        }

        // Live Status Banner (Strictly truthful status)
        item {
            val isRecording = recorderState == RecorderState.RECORDING || recorderState == RecorderState.PAUSED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("status_banner_card"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isRecording) ErrorColor else OutlineColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRecording) {
                        // Pulsing red indicator
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.35f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(ErrorColor.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(ErrorColor)
                            )
                        }
                    } else {
                        // Steady emerald ready dot
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (isAutoRecordEnabled) PrimaryAccent else WarningColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isRecording) {
                                    stringResource(id = R.string.status_recording_active)
                                } else if (isAutoRecordEnabled) {
                                    stringResource(id = R.string.status_telephony_listening)
                                } else {
                                    stringResource(id = R.string.status_standby)
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isRecording) ErrorColor else MainText
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isRecording) ErrorColor.copy(alpha = 0.2f)
                                        else PrimaryAccent.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isRecording) "RECORDING" else if (isAutoRecordEnabled) "AUTO ACTIVE" else "STANDBY",
                                    color = if (isRecording) ErrorColor else PrimaryAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = if (isRecording) {
                                "Time: ${StorageHelper.formatDuration(recorderDurationMs)}"
                            } else if (isAutoRecordEnabled) {
                                "Telephony service monitoring incoming and outgoing calls."
                            } else {
                                "Automatic recording is paused in settings."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = stringResource(id = R.string.stat_total_recordings),
                        value = totalCount.toString(),
                        sub = "Saved on device",
                        accentColor = MainText,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = stringResource(id = R.string.stat_today_recordings),
                        value = todayCount.toString(),
                        sub = "Logged today",
                        accentColor = PrimaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = stringResource(id = R.string.stat_total_duration),
                        value = StorageHelper.formatDuration(totalDurationMs),
                        sub = "Audio recorded",
                        accentColor = MainText,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = stringResource(id = R.string.stat_storage_used),
                        value = StorageHelper.formatBytes(totalStorageBytes),
                        sub = "Free: " + if (availableStorageBytes > 0) StorageHelper.formatBytes(availableStorageBytes) else "OK",
                        accentColor = SecondaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Live Voice Note / Call Memo Bar
        item {
            val isRecording = recorderState == RecorderState.RECORDING || recorderState == RecorderState.PAUSED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memo_recorder_card"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.quick_record_call_memo),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                    Text(
                        text = "Instant hardware mic recorder for call memos and offline voice logs",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isRecording) {
                        // Live Waveform visualizer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF060807))
                                .border(1.dp, OutlineColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val normalizedAmp = (recorderAmplitude.toFloat() / 32767f).coerceIn(0.05f, 1f)
                                val barCount = 30
                                val barWidth = canvasWidth / (barCount * 1.6f)
                                val spacing = barWidth * 0.6f

                                for (i in 0 until barCount) {
                                    val factor = (Math.sin((i.toDouble() + System.currentTimeMillis() / 200.0)) * 0.4 + 0.6).toFloat()
                                    val barHeight = (canvasHeight * 0.8f * normalizedAmp * factor).coerceAtLeast(4f)
                                    val x = i * (barWidth + spacing) + spacing
                                    val y = (canvasHeight - barHeight) / 2

                                    drawRect(
                                        color = Color(0xFF20D68A),
                                        topLeft = Offset(x, y),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                    )
                                }
                            }

                            Text(
                                text = StorageHelper.formatDuration(recorderDurationMs),
                                color = MainText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onStopMemoRecording,
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_stop_memo_record")
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(id = R.string.stop_recording), color = Color.White)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onStartMemoRecording,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_start_memo_record")
                            ) {
                                Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = MainBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Record Memo", color = MainBackground, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onPickAudioToImport,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryAccent),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_import_audio_file")
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Audio")
                            }
                        }
                    }
                }
            }
        }

        // Android OS Compatibility Notice
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDiagnostics() }
                    .testTag("compat_notice_card"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.status_restricted_device),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningColor
                        )
                        Text(
                            text = stringResource(id = R.string.status_restricted_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Details",
                        tint = WarningColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Recent Recordings Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.recent_recordings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Text(
                    text = stringResource(id = R.string.view_all),
                    color = PrimaryAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateToLibrary() }
                        .padding(4.dp)
                        .testTag("btn_view_all_recordings")
                )
            }
        }

        if (recentRecordings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_recent),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(recentRecordings, key = { it.id }) { item ->
                RecentRecordingRow(
                    recording = item,
                    isPlaying = isPlaying && activePlayingId == item.id,
                    onTogglePlay = { onTogglePlayRecording(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    sub: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("metric_card_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontSize = 10.sp)
        }
    }
}

@Composable
fun RecentRecordingRow(
    recording: RecordingEntity,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTogglePlay() }
            .testTag("recent_item_${recording.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call Type Icon
            val (icon, tint) = when (recording.callType.uppercase()) {
                "INCOMING" -> Icons.AutoMirrored.Filled.CallReceived to PrimaryAccent
                "OUTGOING" -> Icons.AutoMirrored.Filled.CallMade to SecondaryAccent
                else -> Icons.Default.Mic to WarningColor
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MainText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(recording.timestamp))
                Text(
                    text = "$dateStr • ${StorageHelper.formatDuration(recording.durationMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_play_recent_${recording.id}")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = PrimaryAccent
                )
            }
        }
    }
}
