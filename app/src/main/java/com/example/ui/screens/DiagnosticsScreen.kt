package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ContainerHigh
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.WarningColor
import com.example.util.DeviceDiagnosticInfo

@Composable
fun DiagnosticsScreen(
    diagnosticInfo: DeviceDiagnosticInfo,
    onRequestPermissions: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp)
            .testTag("diagnostics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.diag_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )
                    Text(
                        text = stringResource(id = R.string.diag_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("btn_refresh_diagnostics")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = PrimaryAccent
                    )
                }
            }
        }

        // Hardware & Platform Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hardware & Runtime Environment",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DiagDetailRow("Android OS Version", "Android ${diagnosticInfo.androidVersion}")
                    DiagDetailRow("API Level", "SDK ${diagnosticInfo.apiLevel}")
                    DiagDetailRow("Manufacturer & Model", "${diagnosticInfo.manufacturer} ${diagnosticInfo.model}")
                    DiagDetailRow(
                        "Voice Communication HAL",
                        if (diagnosticInfo.isVoiceCommSupported) "Hardware Supported" else "Fallback to Mic"
                    )
                }
            }
        }

        // Permissions Status Matrix
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Android System Permissions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionItemRow(
                        title = stringResource(id = R.string.diag_mic_permission),
                        sub = "Required for audio recording capture",
                        isGranted = diagnosticInfo.isMicPermissionGranted
                    )
                    PermissionItemRow(
                        title = stringResource(id = R.string.diag_phone_state_permission),
                        sub = "Detects incoming & outgoing cellular call triggers",
                        isGranted = diagnosticInfo.isPhoneStatePermissionGranted
                    )
                    PermissionItemRow(
                        title = stringResource(id = R.string.diag_contacts_permission),
                        sub = "Matches numbers to local contact names (Zero upload)",
                        isGranted = diagnosticInfo.isContactsPermissionGranted
                    )
                    PermissionItemRow(
                        title = stringResource(id = R.string.diag_notification_permission),
                        sub = "Foreground service recording indicator",
                        isGranted = diagnosticInfo.isNotificationPermissionGranted
                    )

                    val hasMissing = !diagnosticInfo.isMicPermissionGranted ||
                            !diagnosticInfo.isPhoneStatePermissionGranted ||
                            !diagnosticInfo.isContactsPermissionGranted ||
                            !diagnosticInfo.isNotificationPermissionGranted

                    if (hasMissing) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRequestPermissions,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_request_permissions")
                        ) {
                            Text(
                                text = stringResource(id = R.string.diag_request_permissions),
                                color = MainBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Truthful Policy Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ContainerHigh),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.diag_policy_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = R.string.diag_policy_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // OEM-Specific Tips (Samsung, Xiaomi, Pixel)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Device-Specific OEM Recommendations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OemTipItem(
                        brand = "Xiaomi / MIUI / HyperOS",
                        tip = "Open Phone Dialer -> Settings -> Call Recording -> Enable 'Record calls automatically'. Saved files in Recordings/call_rec can be imported into this app."
                    )
                    OemTipItem(
                        brand = "Samsung Galaxy (One UI)",
                        tip = "In regions where enabled, open Phone App -> 3 dots -> Settings -> Record calls -> Auto record. Use our Import Audio tool to index recordings."
                    )
                    OemTipItem(
                        brand = "Google Pixel / Motorola / Stock Android",
                        tip = "On Android 10-15, turn on Speakerphone during calls. Our Voice Communication engine will cleanly capture both your voice and the caller."
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DiagDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MainText)
    }
}

@Composable
fun PermissionItemRow(title: String, sub: String, isGranted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isGranted) PrimaryAccent else ErrorColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MainText)
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontSize = 10.sp)
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isGranted) PrimaryAccent.copy(alpha = 0.15f) else ErrorColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isGranted) "GRANTED" else "MISSING",
                color = if (isGranted) PrimaryAccent else ErrorColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OemTipItem(brand: String, tip: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = brand, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MainText)
        Text(text = tip, style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
