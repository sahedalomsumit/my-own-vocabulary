package com.sahed.my_own_vocabulary.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahed.my_own_vocabulary.data.preferences.QuizFormat
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.AppShapes
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val quizFormat by viewModel.quizFormat.collectAsStateWithLifecycle()
    val mainFolders by viewModel.mainFolders.collectAsStateWithLifecycle()
    val selectedFolderId by viewModel.selectedFolderId.collectAsStateWithLifecycle()
    val quizEntries by viewModel.quizEntries.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val currentMc by viewModel.currentMultipleChoice.collectAsStateWithLifecycle()
    val selectedAnswerIdx by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
    val isCardFlipped by viewModel.isCardFlipped.collectAsStateWithLifecycle()
    val typedAnswer by viewModel.typedAnswer.collectAsStateWithLifecycle()
    val spellingSubmitted by viewModel.spellingSubmitted.collectAsStateWithLifecycle()
    val isSpellingCorrect by viewModel.isSpellingCorrect.collectAsStateWithLifecycle()
    val correctCount by viewModel.correctCount.collectAsStateWithLifecycle()
    val mistakeEntries by viewModel.mistakeEntries.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (quizState) {
                QuizState.IDLE -> {
                    // Quiz Setup & Start Screen
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Practice & Quizzes",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Train your active recall and test your retention",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldTheme.extended.subText
                            )
                        }
                    }

                    // 1. Choose Format
                    item {
                        Text(
                            text = "Choose Game Format",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FormatCard(
                                title = "Multiple Choice",
                                subtitle = "4 answer choices",
                                isSelected = quizFormat == QuizFormat.MULTIPLE_CHOICE,
                                onClick = { viewModel.setQuizFormat(QuizFormat.MULTIPLE_CHOICE) }
                            )
                            FormatCard(
                                title = "Flashcards",
                                subtitle = "Tap to flip & self-rate",
                                isSelected = quizFormat == QuizFormat.FLASHCARD,
                                onClick = { viewModel.setQuizFormat(QuizFormat.FLASHCARD) }
                            )
                            FormatCard(
                                title = "Spelling Test",
                                subtitle = "Type the original word",
                                isSelected = quizFormat == QuizFormat.SPELLING,
                                onClick = { viewModel.setQuizFormat(QuizFormat.SPELLING) }
                            )
                        }
                    }

                    // 2. Choose Scope (Folder)
                    item {
                        Text(
                            text = "Practice Scope",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FolderScopePill(
                                label = "Entire Vocabulary",
                                isSelected = selectedFolderId == null,
                                onClick = { viewModel.selectFolder(null) }
                            )
                            for (folder in mainFolders) {
                                FolderScopePill(
                                    label = folder.name,
                                    isSelected = selectedFolderId == folder.id,
                                    onClick = { viewModel.selectFolder(folder.id) }
                                )
                            }
                        }
                    }

                    // 3. Start Button
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.startQuiz() },
                            shape = AppShapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .bouncyClickable { viewModel.startQuiz() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Psychology,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Practice Session",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                QuizState.ACTIVE -> {
                    // Header with Progress & Exit
                    item {
                        val progress = if (quizEntries.isNotEmpty()) {
                            (currentIndex + 1).toFloat() / quizEntries.size
                        } else 0f

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Word ${currentIndex + 1} of ${quizEntries.size}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                IconButton(onClick = { viewModel.exitQuiz() }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Exit Quiz",
                                        tint = EmeraldTheme.extended.subText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = EmeraldPalette.EmeraldGlow,
                                trackColor = EmeraldTheme.extended.surfaceTier2
                            )
                        }
                    }

                    // Active Game
                    item {
                        when (quizFormat) {
                            QuizFormat.MULTIPLE_CHOICE -> {
                                currentMc?.let { mc ->
                                    MultipleChoiceGame(
                                        mc = mc,
                                        selectedIndex = selectedAnswerIdx,
                                        onSelectOption = { viewModel.submitMultipleChoiceAnswer(it) },
                                        onNext = { viewModel.advanceQuestion() },
                                        onSpeak = { viewModel.speakCurrentWord(mc.entry) }
                                    )
                                }
                            }
                            QuizFormat.FLASHCARD -> {
                                val entry = quizEntries.getOrNull(currentIndex)
                                if (entry != null) {
                                    FlashcardGame(
                                        entry = entry,
                                        isFlipped = isCardFlipped,
                                        onFlip = { viewModel.flipCard() },
                                        onAnswer = { viewModel.answerFlashcard(it) },
                                        onSpeak = { viewModel.speakCurrentWord(entry) }
                                    )
                                }
                            }
                            QuizFormat.SPELLING -> {
                                val entry = quizEntries.getOrNull(currentIndex)
                                if (entry != null) {
                                    SpellingGame(
                                        entry = entry,
                                        typedAnswer = typedAnswer,
                                        isSubmitted = spellingSubmitted,
                                        isCorrect = isSpellingCorrect,
                                        onType = { viewModel.setTypedAnswer(it) },
                                        onSubmit = { viewModel.submitSpellingAnswer() },
                                        onNext = { viewModel.advanceQuestion() },
                                        onSpeak = { viewModel.speakCurrentWord(entry) }
                                    )
                                }
                            }
                        }
                    }
                }

                QuizState.COMPLETED -> {
                    item {
                        QuizSummaryCard(
                            totalQuestions = quizEntries.size,
                            correctCount = correctCount,
                            mistakeCount = mistakeEntries.size,
                            onRetryMistakes = { viewModel.startQuiz(onlyMistakes = true) },
                            onNewQuiz = { viewModel.exitQuiz() }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FormatCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) EmeraldPalette.DeepGreen else EmeraldTheme.extended.surfaceTier1
    val borderColor = if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(95.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .bouncyClickable(onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) EmeraldPalette.EmeraldGlow else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = EmeraldTheme.extended.subText
            )
        }
    }
}

@Composable
private fun FolderScopePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) EmeraldPalette.SoftEmerald else EmeraldTheme.extended.surfaceTier1
    val contentColor = if (isSelected) Color.White else EmeraldTheme.extended.subText
    val borderColor = if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .bouncyClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
