package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText

@Composable
fun SettingsScreen(
    isAutoRecordEnabled: Boolean,
    recordIncoming: Boolean,
    recordOutgoing: Boolean,
    currentLanguage: String,
    isPinEnabled: Boolean,
    onToggleAutoRecord: (Boolean) -> Unit,
    onToggleRecordIncoming: (Boolean) -> Unit,
    onToggleRecordOutgoing: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onSetPin: (String) -> Unit,
    onDisablePin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MainText
            )
        }

        // Automatic Recording Preferences Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.settings_auto_record_group),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingSwitchRow(
                        title = stringResource(id = R.string.settings_enable_auto),
                        desc = stringResource(id = R.string.settings_enable_auto_desc),
                        checked = isAutoRecordEnabled,
                        onCheckedChange = onToggleAutoRecord,
                        testTag = "switch_auto_record"
                    )

                    SettingSwitchRow(
                        title = stringResource(id = R.string.settings_record_incoming),
                        desc = "Record incoming cellular calls when answered",
                        checked = recordIncoming,
                        enabled = isAutoRecordEnabled,
                        onCheckedChange = onToggleRecordIncoming,
                        testTag = "switch_record_incoming"
                    )

                    SettingSwitchRow(
                        title = stringResource(id = R.string.settings_record_outgoing),
                        desc = "Record outgoing calls placed from dialer",
                        checked = recordOutgoing,
                        enabled = isAutoRecordEnabled,
                        onCheckedChange = onToggleRecordOutgoing,
                        testTag = "switch_record_outgoing"
                    )
                }
            }
        }

        // Storage & Audio Configuration Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Storage & Audio Engine",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingInfoRow(
                        title = stringResource(id = R.string.settings_audio_source),
                        value = "Voice Communication (AEC)"
                    )
                    SettingInfoRow(
                        title = stringResource(id = R.string.settings_audio_format),
                        value = "M4A / AAC (High Quality 44.1kHz)"
                    )
                    SettingInfoRow(
                        title = stringResource(id = R.string.settings_storage_location),
                        value = stringResource(id = R.string.settings_storage_location_path)
                    )
                }
            }
        }

        // Language Selection Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.settings_language_group),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LanguageButton(
                            title = "English",
                            isSelected = currentLanguage == "en",
                            onClick = { onLanguageSelected("en") },
                            modifier = Modifier.weight(1f)
                        )
                        LanguageButton(
                            title = "বাংলা (Bengali)",
                            isSelected = currentLanguage == "bn",
                            onClick = { onLanguageSelected("bn") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Security PIN Lock Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.settings_security_group),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.settings_pin_lock),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MainText
                            )
                            Text(
                                text = stringResource(id = R.string.settings_pin_lock_desc),
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryText,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    showPinDialog = true
                                } else {
                                    onDisablePin()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MainBackground,
                                checkedTrackColor = PrimaryAccent,
                                uncheckedTrackColor = OutlineColor
                            ),
                            modifier = Modifier.testTag("switch_pin_lock")
                        )
                    }

                    if (isPinEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showPinDialog = true },
                            modifier = Modifier.testTag("btn_change_pin")
                        ) {
                            Text(stringResource(id = R.string.settings_change_pin), color = PrimaryAccent)
                        }
                    }
                }
            }
        }

        // Privacy Policy & About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.settings_about_group),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.settings_privacy_policy),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(id = R.string.settings_version),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryAccent,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Set PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinInput = ""
                pinError = false
            },
            title = { Text(stringResource(id = R.string.pin_setup_title), color = MainText) },
            text = {
                Column {
                    Text("Enter 4 digits for your security PIN", color = SecondaryText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                        singleLine = true,
                        isError = pinError,
                        placeholder = { Text("4-digit PIN") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MainText,
                            unfocusedTextColor = MainText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pin_setup")
                    )
                    if (pinError) {
                        Text("PIN must be exactly 4 digits", color = ErrorColor, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            onSetPin(pinInput)
                            showPinDialog = false
                            pinInput = ""
                            pinError = false
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text(stringResource(id = R.string.save), color = MainBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    pinInput = ""
                    pinError = false
                }) {
                    Text(stringResource(id = R.string.cancel), color = SecondaryText)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MainText else SecondaryText
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MainBackground,
                checkedTrackColor = PrimaryAccent,
                uncheckedTrackColor = OutlineColor
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun SettingInfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MainText)
    }
}

@Composable
fun LanguageButton(title: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) PrimaryAccent.copy(alpha = 0.2f) else CardBackground,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("lang_btn_${if (title.contains("English")) "en" else "bn"}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PrimaryAccent else MainText
        )
    }
}
