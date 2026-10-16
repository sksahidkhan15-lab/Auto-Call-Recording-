package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RecordingEntity
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
fun LibraryScreen(
    recordings: List<RecordingEntity>,
    searchQuery: String,
    selectedFilter: String,
    selectedSort: String,
    activePlayingId: Long?,
    isPlaying: Boolean,
    onSearchChange: (String) -> Unit,
    onFilterSelect: (String) -> Unit,
    onSortSelect: (String) -> Unit,
    onTogglePlay: (RecordingEntity) -> Unit,
    onToggleStar: (RecordingEntity) -> Unit,
    onRename: (RecordingEntity, String) -> Unit,
    onDelete: (RecordingEntity) -> Unit,
    onShare: (RecordingEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var recordingToRename by remember { mutableStateOf<RecordingEntity?>(null) }
    var recordingToDelete by remember { mutableStateOf<RecordingEntity?>(null) }
    var renameInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp)
            .testTag("library_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(stringResource(id = R.string.search_hint), fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryText)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = SecondaryText)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = OutlineColor,
                focusedTextColor = MainText,
                unfocusedTextColor = MainText
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ALL" to R.string.filter_all,
                "INCOMING" to R.string.filter_incoming,
                "OUTGOING" to R.string.filter_outgoing,
                "MEMO" to R.string.filter_notes,
                "IMPORTED" to R.string.filter_imported,
                "STARRED" to R.string.filter_starred
            )

            filters.forEach { (filterKey, labelRes) ->
                val isSelected = selectedFilter == filterKey
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelect(filterKey) },
                    label = { Text(stringResource(id = labelRes), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryAccent,
                        selectedLabelColor = MainBackground,
                        containerColor = CardBackground,
                        labelColor = SecondaryText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = OutlineColor,
                        selectedBorderColor = PrimaryAccent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("filter_chip_${filterKey.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results count & Sort row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${recordings.size} recordings",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SortChip("NEWEST", selectedSort == "NEWEST") { onSortSelect("NEWEST") }
                SortChip("DURATION", selectedSort == "DURATION") { onSortSelect("DURATION") }
                SortChip("SIZE", selectedSort == "SIZE") { onSortSelect("SIZE") }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recordings List
        if (recordings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = OutlineColor,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.empty_recent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recordings, key = { it.id }) { item ->
                    RecordingCardItem(
                        recording = item,
                        isPlaying = isPlaying && activePlayingId == item.id,
                        onTogglePlay = { onTogglePlay(item) },
                        onToggleStar = { onToggleStar(item) },
                        onRenameClick = {
                            recordingToRename = item
                            renameInputText = item.title
                        },
                        onDeleteClick = { recordingToDelete = item },
                        onShareClick = { onShare(item) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Rename Dialog
    recordingToRename?.let { target ->
        AlertDialog(
            onDismissRequest = { recordingToRename = null },
            title = { Text(stringResource(id = R.string.rename_dialog_title), color = MainText) },
            text = {
                OutlinedTextField(
                    value = renameInputText,
                    onValueChange = { renameInputText = it },
                    placeholder = { Text(stringResource(id = R.string.rename_dialog_hint)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MainText,
                        unfocusedTextColor = MainText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_rename_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRename(target, renameInputText)
                        recordingToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text(stringResource(id = R.string.save), color = MainBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordingToRename = null }) {
                    Text(stringResource(id = R.string.cancel), color = SecondaryText)
                }
            },
            containerColor = CardBackground
        )
    }

    // Delete Confirmation Dialog
    recordingToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { recordingToDelete = null },
            title = { Text(stringResource(id = R.string.delete_confirm_title), color = MainText) },
            text = { Text(stringResource(id = R.string.delete_confirm_msg), color = SecondaryText) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(target)
                        recordingToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) {
                    Text(stringResource(id = R.string.delete_confirm_yes), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordingToDelete = null }) {
                    Text(stringResource(id = R.string.delete_confirm_no), color = SecondaryText)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun SortChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) PrimaryAccent.copy(alpha = 0.2f) else ContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("sort_chip_${label.lowercase()}")
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PrimaryAccent else SecondaryText
        )
    }
}

@Composable
fun RecordingCardItem(
    recording: RecordingEntity,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onToggleStar: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recording_card_${recording.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Type Icon
                val (icon, tint) = when (recording.callType.uppercase()) {
                    "INCOMING" -> Icons.AutoMirrored.Filled.CallReceived to PrimaryAccent
                    "OUTGOING" -> Icons.AutoMirrored.Filled.CallMade to SecondaryAccent
                    "IMPORTED" -> Icons.Default.FileDownload to Color(0xFF8DA4FF)
                    else -> Icons.Default.Mic to WarningColor
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recording.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MainText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val dateFormatted = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()).format(Date(recording.timestamp))
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        fontSize = 11.sp
                    )
                }

                // Star toggle
                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_star_${recording.id}")
                ) {
                    Icon(
                        imageVector = if (recording.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Star",
                        tint = if (recording.isStarred) WarningColor else SecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata row: Duration & Size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "⏱ " + StorageHelper.formatDuration(recording.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryAccent,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "💾 " + StorageHelper.formatBytes(recording.fileSizeBytes),
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        fontSize = 11.sp
                    )
                    recording.phoneNumber?.let {
                        Text(
                            text = "📞 $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryText,
                            fontSize = 11.sp
                        )
                    }
                }

                // Actions: Rename, Share, Delete, Play
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onRenameClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_rename_${recording.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = SecondaryText, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_share_${recording.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SecondaryText, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_delete_${recording.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorColor, modifier = Modifier.size(16.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PrimaryAccent)
                            .clickable { onTogglePlay() }
                            .testTag("btn_play_${recording.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MainBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
