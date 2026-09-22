package com.sahed.my_own_vocabulary.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun VocabBottomBar(
    currentRoute: String,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenAddVocabulary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarHeight = 66.dp
    val fabSize = 52.dp
    val cutoutRadius = 34.dp
    val cornerRadius = 8.dp

    val barColor = EmeraldTheme.extended.surfaceTier1
    val borderColor = EmeraldTheme.extended.glassBorder

    // Breathing pulse for center FAB glow aura
    val infiniteTransition = rememberInfiniteTransition(label = "fabGlowTransition")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabGlowPulse"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navBarHeight)
                    .drawWithCache {
                        val cutoutRadiusPx = cutoutRadius.toPx()
                        val cornerRadiusPx = cornerRadius.toPx()

                        val (bgPath, borderPath) = buildCradlePaths(
                            size = size,
                            cutoutRadius = cutoutRadiusPx,
                            cornerRadius = cornerRadiusPx
                        )

                        onDrawBehind {
                            drawPath(path = bgPath, color = barColor)
                            drawPath(
                                path = borderPath,
                                color = borderColor,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navBarHeight)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Vocabulary Explorer
                    NavPillItem(
                        label = "Vocabulary",
                        selected = currentRoute == "vocabulary",
                        activeIcon = Icons.AutoMirrored.Filled.MenuBook,
                        inactiveIcon = Icons.AutoMirrored.Outlined.MenuBook,
                        onClick = onNavigateToVocabulary,
                        modifier = Modifier.weight(1f)
                    )

                    // 2. Quiz / Practice
                    NavPillItem(
                        label = "Quiz",
                        selected = currentRoute == "quiz",
                        activeIcon = Icons.Filled.Psychology,
                        inactiveIcon = Icons.Outlined.Psychology,
                        onClick = onNavigateToQuiz,
                        modifier = Modifier.weight(1f)
                    )

                    // Center cradle gap for floating Add (+) button
                    Spacer(modifier = Modifier.width(cutoutRadius * 2 + 10.dp))

                    // 3. Calendar Daily Activity
                    NavPillItem(
                        label = "Calendar",
                        selected = currentRoute == "calendar",
                        activeIcon = Icons.Filled.CalendarMonth,
                        inactiveIcon = Icons.Outlined.CalendarMonth,
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )

                    // 4. Settings
                    NavPillItem(
                        label = "Settings",
                        selected = currentRoute == "settings",
                        activeIcon = Icons.Filled.Settings,
                        inactiveIcon = Icons.Outlined.Settings,
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fill system navigation bar area
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(barColor)
                    .navigationBarsPadding()
            )
        }

        // Floating Center Action Button with radiant ambient breathing glow
        val glowSize = 76.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, 0) {
                        placeable.place(0, 0)
                    }
                }
                .offset(y = -(glowSize / 2))
                .size(glowSize),
            contentAlignment = Alignment.Center
        ) {
            // Radiant glow ring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                0.0f to Color(0xFF3DBFA0).copy(alpha = glowPulse),
                                0.40f to Color(0xFF3DBFA0).copy(alpha = glowPulse * 0.75f),
                                0.55f to Color(0xFF2E9C7E).copy(alpha = glowPulse * 0.50f),
                                0.80f to Color(0xFF2E9C7E).copy(alpha = glowPulse * 0.18f),
                                1.0f to Color.Transparent
                            ),
                            radius = size.minDimension / 2f
                        )
                    }
            )

            Surface(
                shape = CircleShape,
                color = EmeraldPalette.SoftEmerald,
                contentColor = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .size(fabSize)
                    .bouncyClickable(
                        pressedScale = 0.90f,
                        onClick = onOpenAddVocabulary
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Vocabulary",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavPillItem(
    label: String,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = EmeraldPalette.EmeraldGlow
    val inactiveColor = EmeraldTheme.extended.subText

    val iconColor by animateColorAsState(
        targetValue = if (selected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 200),
        label = "navIconColor"
    )

    Column(
        modifier = modifier
            .bouncyClickable(pressedScale = 0.92f, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = iconColor,
            maxLines = 1
        )
    }
}

private fun buildCradlePaths(
    size: Size,
    cutoutRadius: Float,
    cornerRadius: Float
): Pair<Path, Path> {
    val cx = size.width / 2f
    val R = cutoutRadius
    val r = cornerRadius

    val leftCornerStartX = cx - R - r
    val leftCutoutStartX = cx - R
    val rightCutoutEndX = cx + R
    val rightCornerEndX = cx + R + r

    val bgPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(leftCornerStartX, 0f)
        // Left rounded corner entering cutout
        arcTo(
            rect = Rect(
                left = leftCornerStartX - r,
                top = 0f,
                right = leftCornerStartX + r,
                bottom = 2 * r
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        // Semicircular cradle cutout
        arcTo(
            rect = Rect(
                left = leftCutoutStartX,
                top = -R,
                right = rightCutoutEndX,
                bottom = R
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        // Right rounded corner exiting cutout
        arcTo(
            rect = Rect(
                left = rightCutoutEndX - r,
                top = 0f,
                right = rightCutoutEndX + r,
                bottom = 2 * r
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    val borderPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(leftCornerStartX, 0f)
        arcTo(
            rect = Rect(
                left = leftCornerStartX - r,
                top = 0f,
                right = leftCornerStartX + r,
                bottom = 2 * r
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        arcTo(
            rect = Rect(
                left = leftCutoutStartX,
                top = -R,
                right = rightCutoutEndX,
                bottom = R
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        arcTo(
            rect = Rect(
                left = rightCutoutEndX - r,
                top = 0f,
                right = rightCutoutEndX + r,
                bottom = 2 * r
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width, 0f)
    }

    return Pair(bgPath, borderPath)
}
