package com.sahed.my_own_vocabulary.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

/**
 * Global background wrapper providing rich greenery color shades and ambient depth.
 * Eliminates plain flat black by blending vertical forest gradients with soft emerald ambient glows.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val extended = EmeraldTheme.extended
    val isDark = extended.isDark

    val verticalBrush = Brush.verticalGradient(
        colors = listOf(
            extended.backgroundTop,
            extended.backgroundCenter,
            extended.backgroundBottom
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = verticalBrush)
    ) {
        // Ambient soft greenery glow aura positioned at the top-center
        val topAuraAlpha = if (isDark) 0.18f else 0.08f
        Box(
            modifier = Modifier
                .size(480.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-90).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmeraldPalette.SoftEmerald.copy(alpha = topAuraAlpha),
                            EmeraldPalette.EmeraldGlow.copy(alpha = topAuraAlpha * 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Subtle ambient secondary greenery aura in the lower area for multi-dimensional depth
        if (isDark) {
            Box(
                modifier = Modifier
                    .size(520.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 120.dp, y = 140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EmeraldPalette.DeepGreen.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        content()
    }
}
