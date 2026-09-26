package com.sahed.my_own_vocabulary.ui.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun HeroStreakCard(
    streakDays: Int,
    wordsLearned: Int,
    wordsMastered: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            EmeraldPalette.DeepGreen,
            EmeraldPalette.Surface1Dark
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .border(
                width = 1.2.dp,
                color = EmeraldTheme.extended.glassBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .then(
                if (onClick != null) Modifier.bouncyClickable(onClick = onClick) else Modifier
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldPalette.WarningAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = EmeraldPalette.WarningAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (streakDays > 0) "$streakDays Day Streak!" else "0 Day Streak",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (streakDays > 0) "Vocabulary added today" else "Add vocabulary today to count",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldTheme.extended.subText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "$wordsLearned",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = EmeraldPalette.EmeraldGlow
                        )
                        Text(
                            text = "Total Words",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldTheme.extended.subText
                        )
                    }

                    Column {
                        Text(
                            text = "$wordsMastered",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = EmeraldPalette.SuccessGreen
                        )
                        Text(
                            text = "Mastered",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldTheme.extended.subText
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                    .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = EmeraldPalette.EmeraldGlow,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun StatMiniCard(
    label: String,
    value: String,
    percent: Float = 0f,
    accentColor: Color = EmeraldPalette.SoftEmerald,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val animatedPercent by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "statMiniProgressAnim"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(EmeraldTheme.extended.surfaceTier1)
            .border(1.dp, EmeraldTheme.extended.glassBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EmeraldTheme.extended.subText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = accentColor
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (percent > 0f) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f)
            )
        }
    }
}
