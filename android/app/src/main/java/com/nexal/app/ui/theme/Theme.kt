package com.nexal.app.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Emerald600,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    secondary = Cyan600,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFcffafe),
    onSecondaryContainer = Cyan900,
    tertiary = Emerald400,
    onTertiary = Color.White,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = Cyan400,
    onSecondary = Cyan900,
    secondaryContainer = Cyan800,
    onSecondaryContainer = Color(0xFFcffafe),
    tertiary = Emerald300,
    onTertiary = Emerald900,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate600,
    outlineVariant = Slate700,
    error = Color(0xFFfca5a5),
    onError = Color(0xFF7f1d1d),
)

/** Global theme mode state holder — survives recomposition, persisted via SharedPreferences */
object ThemeState {
    var isDarkMode: MutableState<Boolean?> = mutableStateOf(null)

    private const val PREFS_NAME = "nexal_theme"
    private const val KEY_DARK = "is_dark_mode"

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_DARK)) {
            isDarkMode.value = prefs.getBoolean(KEY_DARK, false)
        }
        // else stays null → follow system
    }

    fun toggle(context: Context) {
        val newValue = !(isDarkMode.value ?: false)
        isDarkMode.value = newValue
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DARK, newValue).apply()
    }
}

@Composable
fun NexalTheme(
    darkTheme: Boolean = ThemeState.isDarkMode.value ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
