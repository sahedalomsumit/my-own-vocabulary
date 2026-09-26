package com.sahed.my_own_vocabulary.ui.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object EmeraldPalette {
    // Brand Accents
    val SoftEmerald = Color(0xFF2E9C7E)
    val DeepGreen   = Color(0xFF1B3B34)
    val EmeraldGlow = Color(0xFF3DBFA0)
    val EmeraldTint = Color(0x262E9C7E)

    // Secondary & Category Accents
    val AccentPurple = Color(0xFFBA68C8)
    val AccentAmber  = Color(0xFFFF9800)
    val AccentBlue   = Color(0xFF3B82F6)

    // German Article / Gender Colors
    val ArticleDer = Color(0xFF3B82F6) // Masculine - Vibrant Sapphire Blue
    val ArticleDie = Color(0xFFF43F5E) // Feminine - Rose / Crimson
    val ArticleDas = Color(0xFF10B981) // Neuter - Vibrant Emerald

    // Typography Colors (WCAG AAA High Contrast)
    val DarkText     = Color(0xFF111816) // Deep charcoal for light theme
    val LightText    = Color(0xFFFFFFFF) // Pure crisp white for dark theme
    val SubTextGrey  = Color(0xFFAEC4BD) // High-contrast silver-mint secondary text
    val LightSubText = Color(0xFF384E47) // High-contrast deep sage secondary text
    val InactiveGrey = Color(0xFF7A938B) // Muted tone

    // Status Colors
    val SuccessGreen = Color(0xFF4CAF50)
    val WarningAmber = Color(0xFFFF9800)
    val ErrorRed     = Color(0xFFF44336)

    // Dark Surfaces with subtle greenery undertones
    val DarkBackground = Color(0xFF0C1914) // Rich deep forest-emerald dark
    val Surface1Dark   = Color(0xFF13241E) // Elegant dark slate-emerald surface
    val Surface2Dark   = Color(0xFF192E27)
    val Surface3Dark   = Color(0xFF203930)
    val DarkGlassFill  = Color(0x1AFFFFFF) // 10% white
    val DarkGlassBorder= Color(0x33FFFFFF) // 20% white

    // Background Gradient Shades (Atmospheric Greenery Depth)
    val DarkBackgroundGradientTop    = Color(0xFF0F261E) // Rich dark emerald-tinted top shade
    val DarkBackgroundGradientCenter = Color(0xFF0C1914) // Deep forest center shade
    val DarkBackgroundGradientBottom = Color(0xFF08120E) // Grounded night shade

    // Light Surfaces
    val LightBackground = Color(0xFFF3F7F5)
    val Surface1Light   = Color(0xFFFFFFFF)
    val Surface2Light   = Color(0xFFEDF4F1)
    val Surface3Light   = Color(0xFFDCEDE8)
    val LightGlassFill  = Color(0xB3FFFFFF) // 70% white
    val LightGlassBorder= Color(0x66FFFFFF) // 40% white

    val LightBackgroundGradientTop    = Color(0xFFE9F3EE) // Soft sage morning tint
    val LightBackgroundGradientCenter = Color(0xFFF2F7F4) // Gentle mint mist
    val LightBackgroundGradientBottom = Color(0xFFF8FAF9) // Clean light base
}

@Immutable
data class ExtendedColors(
    val surfaceTier1: Color,
    val surfaceTier2: Color,
    val surfaceTier3: Color,
    val glassFill: Color,
    val glassBorder: Color,
    val subText: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val accent: Color,
    val articleDer: Color = EmeraldPalette.ArticleDer,
    val articleDie: Color = EmeraldPalette.ArticleDie,
    val articleDas: Color = EmeraldPalette.ArticleDas,
    val backgroundTop: Color = EmeraldPalette.DarkBackgroundGradientTop,
    val backgroundCenter: Color = EmeraldPalette.DarkBackgroundGradientCenter,
    val backgroundBottom: Color = EmeraldPalette.DarkBackgroundGradientBottom,
    val isDark: Boolean = true
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        surfaceTier1 = EmeraldPalette.Surface1Dark,
        surfaceTier2 = EmeraldPalette.Surface2Dark,
        surfaceTier3 = EmeraldPalette.Surface3Dark,
        glassFill = EmeraldPalette.DarkGlassFill,
        glassBorder = EmeraldPalette.DarkGlassBorder,
        subText = EmeraldPalette.SubTextGrey,
        success = EmeraldPalette.SuccessGreen,
        warning = EmeraldPalette.WarningAmber,
        error = EmeraldPalette.ErrorRed,
        accent = EmeraldPalette.AccentPurple,
        backgroundTop = EmeraldPalette.DarkBackgroundGradientTop,
        backgroundCenter = EmeraldPalette.DarkBackgroundGradientCenter,
        backgroundBottom = EmeraldPalette.DarkBackgroundGradientBottom,
        isDark = true
    )
}
