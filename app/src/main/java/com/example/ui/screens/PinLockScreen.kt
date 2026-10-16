package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.SecondaryText

@Composable
fun PinLockScreen(
    onVerifyPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(24.dp)
            .testTag("pin_lock_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CardBackground)
                .border(1.dp, PrimaryAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = PrimaryAccent,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.pin_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(id = R.string.pin_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4 PIN Dots
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in 0 until 4) {
                val isFilled = i < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) PrimaryAccent else CardBackground)
                        .border(1.dp, if (showError) ErrorColor else OutlineColor, CircleShape)
                )
            }
        }

        if (showError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.pin_error),
                color = ErrorColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Numpad 1-9, 0, Backspace
        val keypad = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        keypad.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.size(64.dp))
                    } else if (key == "DEL") {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CardBackground)
                                .clickable {
                                    if (enteredPin.isNotEmpty()) {
                                        enteredPin = enteredPin.dropLast(1)
                                        showError = false
                                    }
                                }
                                .testTag("btn_pin_del"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete", tint = SecondaryText)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CardBackground)
                                .border(1.dp, OutlineColor, CircleShape)
                                .clickable {
                                    if (enteredPin.length < 4) {
                                        val newPin = enteredPin + key
                                        enteredPin = newPin
                                        showError = false

                                        if (newPin.length == 4) {
                                            val valid = onVerifyPin(newPin)
                                            if (!valid) {
                                                showError = true
                                                enteredPin = ""
                                            }
                                        }
                                    }
                                }
                                .testTag("btn_pin_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainText
                            )
                        }
                    }
                }
            }
        }
    }
}
