package com.nexal.app.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val SoftDarkSurface = Color(0xFF121914)
private val SoftDarkVariant = Color(0xFF1A241D)
private val SoftDarkOutline = Color(0xFF344239)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentWash,
    onPrimaryContainer = AccentDeep,
    secondary = Emerald500,
    onSecondary = Color.White,
    secondaryContainer = Emerald100,
    onSecondaryContainer = Emerald900,
    tertiary = MacroFat,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = CreamSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE9ECE5),
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBright,
    onPrimary = Color(0xFF04231A),
    primaryContainer = Color(0xFF06503B),
    onPrimaryContainer = AccentWash,
    secondary = Cyan400,
    onSecondary = Cyan900,
    secondaryContainer = Cyan800,
    onSecondaryContainer = Emerald100,
    tertiary = Color(0xFFFF8FA3),
    onTertiary = Color(0xFF5C1224),
    background = Slate950,
    onBackground = Slate100,
    surface = SoftDarkSurface,
    onSurface = Slate100,
    surfaceVariant = SoftDarkVariant,
    onSurfaceVariant = Slate400,
    outline = SoftDarkOutline,
    outlineVariant = SoftDarkVariant,
    error = Color(0xFFFF8A8A),
    onError = Color(0xFF5C1A1A),
)

val NexalShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

object ThemeState {
    /** null = not loaded yet; default diary experience is light */
    var isDarkMode: MutableState<Boolean?> = mutableStateOf(false)

    private const val PREFS_NAME = "nexal_theme"
    private const val KEY_DARK = "is_dark_mode"
    private const val KEY_LIGHT_DIARY_V1 = "light_diary_default_v1"

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // One-time: ship light diary as the default look for this redesign.
        if (!prefs.getBoolean(KEY_LIGHT_DIARY_V1, false)) {
            isDarkMode.value = false
            prefs.edit()
                .putBoolean(KEY_DARK, false)
                .putBoolean(KEY_LIGHT_DIARY_V1, true)
                .apply()
            return
        }
        isDarkMode.value = prefs.getBoolean(KEY_DARK, false)
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
    darkTheme: Boolean = ThemeState.isDarkMode.value ?: false,
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
        shapes = NexalShapes,
        content = content
    )
}
