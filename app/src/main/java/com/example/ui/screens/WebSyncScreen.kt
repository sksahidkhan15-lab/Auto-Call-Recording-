package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.bridge.WebAppBridge
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ContainerHigh
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryText

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebSyncScreen(
    bridge: WebAppBridge,
    onExportJson: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .testTag("web_sync_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardBackground,
            contentColor = PrimaryAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryAccent
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Embedded Web App", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("GitHub & Backup", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Live Android WebView loading local bundled web app
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {}
                        addJavascriptInterface(bridge, "AndroidBridge")
                        loadUrl("file:///android_asset/web/index.html")
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_component")
            )
        } else {
            // GitHub Pages & Metadata Backup Guide
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export Metadata Backup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MainText
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Generate a JSON backup of all recordings metadata, timestamps, contact names, and call types for import into the Web Companion or archiving.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExportJson,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_export_metadata_json")
                        ) {
                            Text("Export Backup JSON", color = MainBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ContainerHigh),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Deploy to GitHub Pages for Free",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MainText
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. Create a public repository on GitHub (e.g. 'auto-call-recorder').\n" +
                                   "2. Push the root 'index.html', 'style.css', and 'app.js'.\n" +
                                   "3. In GitHub repo Settings -> Pages -> select branch 'main' -> Save.\n" +
                                   "4. Your offline web player will be live worldwide instantly with HTTPS!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
