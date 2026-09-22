package com.sahed.my_own_vocabulary.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.ui.components.BuiltBySahedFooter
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.HeroStreakCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.components.gentleEntrance
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    viewModel: VocabularyViewModel,
    onEditEntry: (VocabularyEntryEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val entries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val mainFolders by viewModel.mainFolders.collectAsStateWithLifecycle()
    val subFolders by viewModel.subFoldersForSelectedMain.collectAsStateWithLifecycle()
    val selectedMainId by viewModel.selectedMainFolderId.collectAsStateWithLifecycle()
    val selectedSubId by viewModel.selectedSubFolderId.collectAsStateWithLifecycle()
    val showFavsOnly by viewModel.showFavoritesOnly.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val totalWords by viewModel.totalWordsCount.collectAsStateWithLifecycle()
    val masteredWords by viewModel.masteredWordsCount.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()

    var entryToDelete by remember { mutableStateOf<VocabularyEntryEntity?>(null) }

    // Delete confirmation dialog
    entryToDelete?.let { entry ->
        EmeraldAlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = "Delete Word",
            content = {
                Text(
                    text = "Are you sure you want to delete \"${entry.originalWord}\" (${entry.translatedWord})?",
                    color = EmeraldTheme.extended.subText
                )
            },
            confirmButtonText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteEntry(entry)
                entryToDelete = null
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Header Banner & Stats
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "My Own Vocabulary",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Master words effortlessly every day",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldTheme.extended.subText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                                .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.3f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = EmeraldPalette.EmeraldGlow,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$totalWords words",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPalette.EmeraldGlow
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hero Streak & Progress Card
                    HeroStreakCard(
                        streakDays = streakDays,
                        wordsLearned = totalWords,
                        wordsMastered = masteredWords
                    )
                }
            }

            // 2. Search Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search words, translations, notes...",
                            color = EmeraldTheme.extended.subText
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldTheme.extended.subText
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = EmeraldTheme.extended.subText
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EmeraldTheme.extended.surfaceTier1,
                        unfocusedContainerColor = EmeraldTheme.extended.surfaceTier1,
                        focusedBorderColor = EmeraldPalette.EmeraldGlow,
                        unfocusedBorderColor = EmeraldTheme.extended.glassBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Horizontal Filter Chips (Main Folders & Favorites)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All" Pill
                        FilterPill(
                            label = "All",
                            isSelected = selectedMainId == null && !showFavsOnly,
                            onClick = {
                                if (showFavsOnly) viewModel.toggleFavoritesOnly()
                                viewModel.selectMainFolder(null)
                            }
                        )

                        // "Favorites ⭐" Pill
                        FilterPill(
                            label = "Favorites ⭐",
                            isSelected = showFavsOnly,
                            onClick = { viewModel.toggleFavoritesOnly() }
                        )

                        // Main Folder Pills
                        for (folder in mainFolders) {
                            val flag = com.sahed.my_own_vocabulary.util.LanguageRegistry.getFlagForCode(folder.sourceLanguage)
                            FilterPill(
                                label = "$flag ${folder.name}",
                                isSelected = selectedMainId == folder.id,
                                onClick = { viewModel.selectMainFolder(folder.id) }
                            )
                        }
                    }

                    // Sub-Folder Pills row (if a Main Folder is selected)
                    if (subFolders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterPill(
                                label = "All Sub-folders",
                                isSelected = selectedSubId == null,
                                isSubLevel = true,
                                onClick = { viewModel.selectSubFolder(null) }
                            )
                            for (sub in subFolders) {
                                FilterPill(
                                    label = sub.name,
                                    isSelected = selectedSubId == sub.id,
                                    isSubLevel = true,
                                    onClick = { viewModel.selectSubFolder(sub.id) }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Word List Section
            if (entries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Rounded.Folder,
                                contentDescription = null,
                                tint = EmeraldTheme.extended.subText.copy(alpha = 0.5f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No vocabulary found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Try another search keyword"
                                else "Tap the '+' button below to add your first word!",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldTheme.extended.subText
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = entries,
                    key = { _, item -> item.id }
                ) { index, entry ->
                    Box(modifier = Modifier.gentleEntrance(index = index)) {
                        VocabularyCard(
                            entry = entry,
                            onSpeak = { viewModel.speakWord(entry) },
                            onToggleFavorite = { viewModel.toggleFavorite(entry) },
                            onEdit = { onEditEntry(entry) },
                            onDelete = { entryToDelete = entry }
                        )
                    }
                }
            }

            // Footer Spacer
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    isSubLevel: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) {
        if (isSubLevel) EmeraldPalette.DeepGreen else EmeraldPalette.SoftEmerald
    } else {
        EmeraldTheme.extended.surfaceTier1
    }
    val contentColor = if (isSelected) Color.White else EmeraldTheme.extended.subText
    val borderColor = if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .bouncyClickable(
                pressedScale = 0.94f,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = if (isSubLevel) 12.sp else 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
