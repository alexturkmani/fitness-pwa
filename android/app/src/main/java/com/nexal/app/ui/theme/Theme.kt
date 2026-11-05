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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val SoftDarkSurface = Color(0xFF141518)
private val SoftDarkVariant = Color(0xFF1D1F23)
private val SoftDarkOutline = Color(0xFF34373C)

private val LightColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECECEA),
    onPrimaryContainer = Ink,
    secondary = Slate600,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECECEA),
    onSecondaryContainer = Ink,
    tertiary = MacroFat,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = CreamSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFECECEA),
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF2F3F5),
    onPrimary = Slate900,
    primaryContainer = Color(0xFF26282D),
    onPrimaryContainer = Slate100,
    secondary = Slate300,
    onSecondary = Slate900,
    secondaryContainer = Color(0xFF26282D),
    onSecondaryContainer = Slate100,
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
    /** null = not loaded yet; the photo-led design defaults to dark */
    var isDarkMode: MutableState<Boolean?> = mutableStateOf(true)

    private const val PREFS_NAME = "nexal_theme"
    private const val KEY_DARK = "is_dark_mode"
    private const val KEY_GLASS_DARK_V1 = "glass_dark_default_v1"

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // One-time: move everyone to dark for this redesign; the toggle still works after.
        if (!prefs.getBoolean(KEY_GLASS_DARK_V1, false)) {
            isDarkMode.value = true
            prefs.edit()
                .putBoolean(KEY_DARK, true)
                .putBoolean(KEY_GLASS_DARK_V1, true)
                .apply()
            return
        }
        isDarkMode.value = prefs.getBoolean(KEY_DARK, true)
    }

    fun toggle(context: Context) {
        val newValue = !(isDarkMode.value ?: true)
        isDarkMode.value = newValue
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DARK, newValue).apply()
    }
}

@Composable
fun NexalTheme(
    darkTheme: Boolean = ThemeState.isDarkMode.value ?: true,
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

/** The bright macro colours fade out on the light theme, so darken them for text and icons there. */
@Composable
fun readable(color: Color): Color =
    if (MaterialTheme.colorScheme.background.luminance() > 0.5f) lerp(color, Ink, 0.55f) else color

/** The accent line colour: lime on dark surfaces, the deeper brand green where lime would vanish on white. */
@Composable
fun limeEdge(): Color =
    if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Accent else AccentBright
