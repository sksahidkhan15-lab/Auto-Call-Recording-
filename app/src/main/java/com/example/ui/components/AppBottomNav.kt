package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.MainBackground
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryText

@Composable
fun AppBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("app_bottom_nav"),
        containerColor = CardBackground,
        contentColor = PrimaryAccent
    ) {
        val navItems = listOf(
            Triple(AppScreen.DASHBOARD, R.string.nav_dashboard, Icons.Default.Dashboard),
            Triple(AppScreen.LIBRARY, R.string.nav_recordings, Icons.Default.Folder),
            Triple(AppScreen.DIAGNOSTICS, R.string.nav_diagnostics, Icons.Default.Info),
            Triple(AppScreen.WEB_SYNC, R.string.nav_web_sync, Icons.Default.Public),
            Triple(AppScreen.SETTINGS, R.string.nav_settings, Icons.Default.Settings)
        )

        navItems.forEach { (screen, labelRes, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = labelRes),
                        color = if (isSelected) PrimaryAccent else SecondaryText
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainBackground,
                    indicatorColor = PrimaryAccent,
                    unselectedIconColor = SecondaryText
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}
