package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecordingEntity
import com.example.ui.theme.ContainerHigh
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryText
import com.example.util.StorageHelper

@Composable
fun AudioPlayerBottomBar(
    activeRecording: RecordingEntity?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    playbackSpeed: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onRewind5s: () -> Unit,
    onForward5s: () -> Unit,
    onCycleSpeed: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeRecording == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_bottom_bar"),
        colors = CardDefaults.cardColors(containerColor = ContainerHigh),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Header Row: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeRecording.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MainText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = (activeRecording.phoneNumber?.let { "$it • " } ?: "") +
                                StorageHelper.formatDuration(activeRecording.durationMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("player_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Player",
                        tint = SecondaryText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Timeline Slider Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StorageHelper.formatDuration(currentPositionMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontSize = 10.sp
                )

                val effectiveDuration = if (durationMs > 0) durationMs else activeRecording.durationMs
                val progress = if (effectiveDuration > 0) {
                    (currentPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Slider(
                    value = progress,
                    onValueChange = { frac ->
                        onSeek((frac * effectiveDuration).toLong())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("player_seek_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryAccent,
                        activeTrackColor = PrimaryAccent,
                        inactiveTrackColor = OutlineColor
                    )
                )

                Text(
                    text = StorageHelper.formatDuration(effectiveDuration),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontSize = 10.sp
                )
            }

            // Controls Row: Speed, Rewind, Play/Pause, Forward
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Chip Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OutlineColor)
                        .clickable { onCycleSpeed() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("player_speed_btn")
                ) {
                    Text(
                        text = "${playbackSpeed}x",
                        color = MainText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onRewind5s,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .testTag("player_rewind_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 5 seconds",
                        tint = MainText
                    )
                }

                // Main Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryAccent)
                        .clickable { onTogglePlay() }
                        .testTag("player_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MainBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onForward5s,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .testTag("player_forward_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 5 seconds",
                        tint = MainText
                    )
                }
            }
        }
    }
}
