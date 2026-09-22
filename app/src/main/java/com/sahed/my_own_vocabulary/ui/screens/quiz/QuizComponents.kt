package com.sahed.my_own_vocabulary.ui.screens.quiz

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.AppShapes
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun MultipleChoiceGame(
    mc: MultipleChoiceQuestion,
    selectedIndex: Int?,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Word Question Card
        EmeraldGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Article Pill if present
                mc.entry.articleOrGender?.takeIf { it.isNotBlank() }?.let { article ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.2f))
                            .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = article,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPalette.EmeraldGlow
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mc.entry.originalWord,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                            .bouncyClickable(onClick = onSpeak),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Speak",
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Choose the correct translation",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldTheme.extended.subText
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4 Options
        mc.options.forEachIndexed { index, optionText ->
            val isSelected = selectedIndex == index
            val isCorrectAnswer = index == mc.correctIndex
            val hasAnswered = selectedIndex != null

            val (bgColor, borderColor, textColor) = when {
                !hasAnswered -> Triple(
                    EmeraldTheme.extended.surfaceTier1,
                    EmeraldTheme.extended.glassBorder,
                    MaterialTheme.colorScheme.onSurface
                )
                isCorrectAnswer -> Triple(
                    EmeraldPalette.SuccessGreen.copy(alpha = 0.25f),
                    EmeraldPalette.SuccessGreen,
                    EmeraldPalette.SuccessGreen
                )
                isSelected && !isCorrectAnswer -> Triple(
                    EmeraldPalette.ErrorRed.copy(alpha = 0.25f),
                    EmeraldPalette.ErrorRed,
                    EmeraldPalette.ErrorRed
                )
                else -> Triple(
                    EmeraldTheme.extended.surfaceTier1.copy(alpha = 0.5f),
                    EmeraldTheme.extended.glassBorder.copy(alpha = 0.4f),
                    EmeraldTheme.extended.subText
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
                    .then(
                        if (!hasAnswered) Modifier.bouncyClickable { onSelectOption(index) }
                        else Modifier
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = textColor
                    )

                    if (hasAnswered && isCorrectAnswer) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Correct",
                            tint = EmeraldPalette.SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (hasAnswered && isSelected && !isCorrectAnswer) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Incorrect",
                            tint = EmeraldPalette.ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Next Button if answered
        if (selectedIndex != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNext,
                shape = AppShapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bouncyClickable(onClick = onNext)
            ) {
                Text(
                    text = "Next Question ➔",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FlashcardGame(
    entry: VocabularyEntryEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onAnswer: (Boolean) -> Unit,
    onSpeak: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flashcardFlipAnim"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Flip Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(22.dp))
                .background(EmeraldTheme.extended.surfaceTier1)
                .border(1.5.dp, EmeraldTheme.extended.glassBorder, RoundedCornerShape(22.dp))
                .clickable { onFlip() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front: Original Word
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    entry.articleOrGender?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPalette.EmeraldGlow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = entry.originalWord,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.2f))
                            .bouncyClickable(onClick = onSpeak),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Speak",
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Tap to reveal translation ↺",
                        fontSize = 12.sp,
                        color = EmeraldTheme.extended.subText
                    )
                }
            } else {
                // Back: Translation + Examples (flipped 180 so text is normal)
                Column(
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = entry.translatedWord,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPalette.EmeraldGlow
                        ),
                        textAlign = TextAlign.Center
                    )
                    entry.exampleSentence?.takeIf { it.isNotBlank() }?.let { ex ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"$ex\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = EmeraldTheme.extended.subText,
                            textAlign = TextAlign.Center
                        )
                    }
                    entry.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = notes,
                            fontSize = 12.sp,
                            color = EmeraldTheme.extended.subText.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Response Buttons (Active once flipped)
        if (isFlipped) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onAnswer(false) },
                    shape = AppShapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.ErrorRed.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .bouncyClickable { onAnswer(false) }
                ) {
                    Text("Need Practice", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { onAnswer(true) },
                    shape = AppShapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .bouncyClickable { onAnswer(true) }
                ) {
                    Text("Got It! ✓", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SpellingGame(
    entry: VocabularyEntryEntity,
    typedAnswer: String,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    onType: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EmeraldGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = entry.translatedWord,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPalette.EmeraldGlow,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Type the original word in ${entry.sourceLanguage.uppercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldTheme.extended.subText
                )
            }
        }

        OutlinedTextField(
            value = typedAnswer,
            onValueChange = onType,
            placeholder = { Text("Enter spelling...", color = EmeraldTheme.extended.subText) },
            singleLine = true,
            enabled = !isSubmitted,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = EmeraldTheme.extended.surfaceTier1,
                unfocusedContainerColor = EmeraldTheme.extended.surfaceTier1,
                focusedBorderColor = EmeraldPalette.EmeraldGlow,
                unfocusedBorderColor = EmeraldTheme.extended.glassBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isSubmitted) {
            Button(
                onClick = onSubmit,
                enabled = typedAnswer.isNotBlank(),
                shape = AppShapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .bouncyClickable(onClick = onSubmit)
            ) {
                Text("Check Spelling", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            // Result Feedback Box
            val (boxBg, boxBorder, statusText) = if (isCorrect) {
                Triple(EmeraldPalette.SuccessGreen.copy(alpha = 0.2f), EmeraldPalette.SuccessGreen, "Correct! Well done!")
            } else {
                Triple(EmeraldPalette.ErrorRed.copy(alpha = 0.2f), EmeraldPalette.ErrorRed, "Correct answer: ${entry.originalWord}")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boxBg)
                    .border(1.dp, boxBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isCorrect) EmeraldPalette.SuccessGreen else EmeraldPalette.ErrorRed
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.2f))
                            .bouncyClickable(onClick = onSpeak),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Speak",
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onNext,
                shape = AppShapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .bouncyClickable(onClick = onNext)
            ) {
                Text("Next Word ➔", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun QuizSummaryCard(
    totalQuestions: Int,
    correctCount: Int,
    mistakeCount: Int,
    onRetryMistakes: () -> Unit,
    onNewQuiz: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0

    EmeraldGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quiz Completed!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Great practice session for your memory",
                style = MaterialTheme.typography.bodySmall,
                color = EmeraldTheme.extended.subText
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Score Circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                    .border(2.dp, EmeraldPalette.EmeraldGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = EmeraldPalette.EmeraldGlow
                    )
                    Text(
                        text = "Accuracy",
                        fontSize = 11.sp,
                        color = EmeraldTheme.extended.subText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$correctCount",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPalette.SuccessGreen
                    )
                    Text(
                        text = "Correct",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldTheme.extended.subText
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$mistakeCount",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPalette.ErrorRed
                    )
                    Text(
                        text = "Mistakes",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldTheme.extended.subText
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (mistakeCount > 0) {
                Button(
                    onClick = onRetryMistakes,
                    shape = AppShapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.WarningAmber),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .bouncyClickable(onClick = onRetryMistakes)
                ) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry Mistakes ($mistakeCount)", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onNewQuiz,
                shape = AppShapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .bouncyClickable(onClick = onNewQuiz)
            ) {
                Text("Start New Quiz", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
