package org.gabrielsantana.tasks.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import org.gabrielsantana.tasks.features.settings.ColorType

@Stable
class AppState {
    var themeMode by mutableStateOf<ThemeMode>(ThemeMode.System)
    var isDynamicColorsEnabled by mutableStateOf(false)
}

val AppState.isDarkMode: Boolean
    @Composable
    get() {
        return if (themeMode is ThemeMode.System) isSystemInDarkTheme() else themeMode is ThemeMode.Dark
    }

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}


@Composable
fun rememberAppState(): AppState = remember {
    AppState()
}