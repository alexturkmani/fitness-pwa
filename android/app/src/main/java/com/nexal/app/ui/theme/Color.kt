package com.nexal.app.ui.theme

import androidx.compose.ui.graphics.Color

/** Nexal launch identity: warm ivory, near-black ink, emerald and electric lime. */

// Brand blue (primary actions / nav)
val Emerald50 = Color(0xFFF0FAF4)
val Emerald100 = Color(0xFFDDF5E7)
val Emerald200 = Color(0xFFB8EACD)
val Emerald300 = Color(0xFF80DBA9)
val Emerald400 = Color(0xFF44C985)
val Emerald500 = Color(0xFF20B66F)
val Emerald600 = Color(0xFF159B63)
val Emerald700 = Color(0xFF117B50)
val Emerald800 = Color(0xFF115F42)
val Emerald900 = Color(0xFF0E4E37)

// Secondary mint family keeps water and supporting metrics on-brand.
val Cyan400 = Color(0xFF77DDB2)
val Cyan500 = Color(0xFF28D58A)
val Cyan600 = Color(0xFF20B66F)
val Cyan700 = Color(0xFF159B63)
val Cyan800 = Color(0xFF115F42)
val Cyan900 = Color(0xFF0E4E37)

// Cool neutrals
val Slate50 = Color(0xFFF4F5EF)
val Slate100 = Color(0xFFE9ECE5)
val Slate200 = Color(0xFFD7DDD5)
val Slate300 = Color(0xFFBCC5BD)
val Slate400 = Color(0xFF8A968D)
val Slate500 = Color(0xFF667168)
val Slate600 = Color(0xFF4C574F)
val Slate700 = Color(0xFF354139)
val Slate800 = Color(0xFF222B25)
val Slate900 = Color(0xFF141B16)
val Slate950 = Color(0xFF0B100D)

// Surfaces
val Cream = Color(0xFFF4F5EF)
val CreamSurface = Color(0xFFFCFDF9)
val Ink = Color(0xFF101713)

// Classic diary macro colors
val MacroProtein = Color(0xFFC9F755)
val MacroCarbs = Color(0xFF28D58A)
val MacroFat = Color(0xFF77DDB2)
val MacroCalorie = Color(0xFFC9F755)

// Semantic
val ErrorRed = Color(0xFFE5484D)
val WarningAmber = Color(0xFFF5A524)
val SuccessGreen = Color(0xFF30A46C)

// ── Single-accent design pass ────────────────────────────────────────────────
// One hot accent (the green from the Nexal mark) carries every primary action;
// everything else is neutral. Two tones because the bright green fails text
// contrast on white — bright is for fills and graphics, deep is for type.
val AccentBright = Color(0xFFC9F755)
val Accent = Color(0xFF159B63)
val AccentDeep = Color(0xFF117B50)
val AccentWash = Color(0xFFE5F7EC)

// Near-black hero surface used for the one card that matters per screen
val HeroInk = Color(0xFF121914)

val BrandBlue = Accent
val BrandBlueDark = AccentDeep
