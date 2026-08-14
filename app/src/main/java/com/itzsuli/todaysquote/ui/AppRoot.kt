package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itzsuli.todaysquote.data.QuoteRepository

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    TODAY("today", "Today", Icons.Outlined.AutoAwesome),
    LIBRARY("library", "Library", Icons.Outlined.LibraryBooks),
    WIDGETS("widgets", "Widgets", Icons.Outlined.Widgets),
    SETTINGS("settings", "Settings", Icons.Outlined.Tune)
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val repo = QuoteRepository.get(context)
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val current = entry?.destination?.route

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0E131D),
                        MaterialTheme.colorScheme.background,
                        Color(0xFF0A0D14)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xE6141926),
                    tonalElevation = 0.dp
                ) {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = current == tab.route,
                            onClick = {
                                if (current != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(Tab.TODAY.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color(0xFF1B2231),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { insets ->
            NavHost(
                navController = navController,
                startDestination = Tab.TODAY.route,
                modifier = Modifier.padding(bottom = insets.calculateBottomPadding())
            ) {
                composable(Tab.TODAY.route) { TodayScreen(repo) }
                composable(Tab.LIBRARY.route) { LibraryScreen(repo) }
                composable(Tab.WIDGETS.route) { WidgetStudioScreen(repo = repo) }
                composable(Tab.SETTINGS.route) { SettingsScreen(repo) }
            }
        }
    }
}
