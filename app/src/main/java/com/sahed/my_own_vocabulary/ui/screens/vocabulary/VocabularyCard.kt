package com.sahed.my_own_vocabulary.ui.screens.vocabulary

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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun VocabularyCard(
    entry: VocabularyEntryEntity,
    onSpeak: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmeraldGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Word + Article Badge + Favorite Star + Audio Speaker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // German Article / Gender Badge
                    entry.articleOrGender?.takeIf { it.isNotBlank() }?.let { article ->
                        val articleColor = when (article.lowercase().trim()) {
                            "der", "masculine", "m" -> EmeraldPalette.ArticleDer
                            "die", "feminine", "f" -> EmeraldPalette.ArticleDie
                            "das", "neuter", "n" -> EmeraldPalette.ArticleDas
                            else -> EmeraldTheme.extended.surfaceTier3
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(articleColor.copy(alpha = 0.2f))
                                .border(1.dp, articleColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = article,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = articleColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Original Word
                    Text(
                        text = entry.originalWord,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Action Buttons: Audio Pronunciation, Favorite, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speaker Audio Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                            .bouncyClickable(
                                pressedScale = 0.85f,
                                onClick = onSpeak
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Favorite Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .bouncyClickable(
                                pressedScale = 0.85f,
                                onClick = onToggleFavorite
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (entry.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (entry.isFavorite) EmeraldPalette.WarningAmber else EmeraldTheme.extended.subText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Edit Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .bouncyClickable(
                                pressedScale = 0.85f,
                                onClick = onEdit
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint = EmeraldTheme.extended.subText.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Delete Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .bouncyClickable(
                                pressedScale = 0.85f,
                                onClick = onDelete
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EmeraldTheme.extended.subText.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Translation Word
            Text(
                text = entry.translatedWord,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = EmeraldPalette.EmeraldGlow
            )

            // Example Sentence (if any)
            entry.exampleSentence?.takeIf { it.isNotBlank() }?.let { example ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"$example\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = EmeraldTheme.extended.subText
                )
            }

            // Notes (if any)
            entry.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: $notes",
                    fontSize = 11.sp,
                    color = EmeraldTheme.extended.subText.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Tag Row: Folder / Sub-Folder Pill + Mastery Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Folder & Subfolder Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Folder,
                        contentDescription = null,
                        tint = EmeraldTheme.extended.subText,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val flag = com.sahed.my_own_vocabulary.util.LanguageRegistry.getFlagForCode(entry.sourceLanguage)
                    val folderPath = when {
                        entry.subSubFolderName != null -> "$flag ${entry.mainFolderName} › ${entry.subFolderName} › ${entry.subSubFolderName}"
                        entry.subFolderName != null -> "$flag ${entry.mainFolderName} › ${entry.subFolderName}"
                        else -> "$flag ${entry.mainFolderName}"
                    }
                    Text(
                        text = folderPath,
                        fontSize = 11.sp,
                        color = EmeraldTheme.extended.subText
                    )
                }

                // Mastery Status Badge
                val (masteryText, masteryColor) = when (entry.masteryLevel) {
                    2 -> "Mastered" to EmeraldPalette.SuccessGreen
                    1 -> "Learning" to EmeraldPalette.WarningAmber
                    else -> "New" to EmeraldTheme.extended.subText
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(masteryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = masteryText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = masteryColor
                    )
                }
            }
        }
    }
}
