package com.sahed.my_own_vocabulary.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sahed.my_own_vocabulary.ui.components.MainFolderLanguageDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.AppShapes
import com.sahed.my_own_vocabulary.ui.designsystem.theme.BottomSheetShape
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme
import com.sahed.my_own_vocabulary.util.LanguageRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVocabularySheet(
    viewModel: AddVocabularyViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val mainFolders by viewModel.mainFolders.collectAsStateWithLifecycle()
    val selectedMain by viewModel.selectedMainFolder.collectAsStateWithLifecycle()
    val subFolders by viewModel.subFoldersForSelectedMain.collectAsStateWithLifecycle()
    val selectedSub by viewModel.selectedSubFolder.collectAsStateWithLifecycle()
    val subSubFolders by viewModel.subSubFoldersForSelectedSub.collectAsStateWithLifecycle()
    val selectedSubSub by viewModel.selectedSubSubFolder.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()

    val origWord by viewModel.originalWord.collectAsStateWithLifecycle()
    val transWord by viewModel.translatedWord.collectAsStateWithLifecycle()
    val article by viewModel.articleOrGender.collectAsStateWithLifecycle()
    val example by viewModel.exampleSentence.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    var showNewMainDialog by remember { mutableStateOf(false) }
    var newMainName by remember { mutableStateOf("") }

    var showNewSubDialog by remember { mutableStateOf(false) }
    var newSubName by remember { mutableStateOf("") }

    var showNewSubSubDialog by remember { mutableStateOf(false) }
    var newSubSubName by remember { mutableStateOf("") }

    // Close sheet when saved
    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collect {
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        if (selectedMain == null) {
            viewModel.restoreLastUsedFolders()
        }
    }

    // Dialog: Create New Main Folder with Language Flag Picker & Search
    if (showNewMainDialog) {
        MainFolderLanguageDialog(
            onDismissRequest = { showNewMainDialog = false },
            ttsManager = viewModel.ttsManager,
            onConfirm = { name, sourceLang, targetLang ->
                viewModel.createNewMainFolder(name, sourceLang, targetLang)
                showNewMainDialog = false
            }
        )
    }

    // Dialog: Create New Sub-Folder
    if (showNewSubDialog) {
        EmeraldAlertDialog(
            onDismissRequest = { showNewSubDialog = false },
            title = "New Sub-Folder in ${selectedMain?.name ?: ""}",
            content = {
                OutlinedTextField(
                    value = newSubName,
                    onValueChange = { newSubName = it },
                    label = { Text("Sub-Folder Name") },
                    placeholder = { Text("e.g. Food, Irregular Verbs") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Create",
            onConfirm = {
                if (newSubName.isNotBlank()) {
                    viewModel.createNewSubFolder(newSubName)
                    newSubName = ""
                    showNewSubDialog = false
                }
            }
        )
    }

    // Dialog: Create New Sub-Sub Folder
    if (showNewSubSubDialog) {
        EmeraldAlertDialog(
            onDismissRequest = { showNewSubSubDialog = false },
            title = "New Sub-Sub Folder in ${selectedSub?.name ?: ""}",
            content = {
                OutlinedTextField(
                    value = newSubSubName,
                    onValueChange = { newSubSubName = it },
                    label = { Text("Sub-Sub Folder Name") },
                    placeholder = { Text("e.g. Unit 1, Advanced") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Create",
            onConfirm = {
                if (newSubSubName.isNotBlank()) {
                    viewModel.createNewSubSubFolder(newSubSubName)
                    newSubSubName = ""
                    showNewSubSubDialog = false
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetShape,
        containerColor = EmeraldTheme.extended.surfaceTier1,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(EmeraldTheme.extended.glassBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit Vocabulary Word" else "Add Vocabulary Word",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Listen preview button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                        .bouncyClickable(onClick = { viewModel.previewPronunciation() })
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Test Audio",
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Listen Preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPalette.EmeraldGlow
                        )
                    }
                }
            }

            // 1. Select Main Folder
            Column {
                Text(
                    text = "Main Folder",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldTheme.extended.subText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (folder in mainFolders) {
                        val isSelected = selectedMain?.id == folder.id
                        val flag = LanguageRegistry.getFlagForCode(folder.sourceLanguage)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPalette.SoftEmerald else EmeraldTheme.extended.surfaceTier2)
                                .border(1.dp, if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder, RoundedCornerShape(10.dp))
                                .bouncyClickable { viewModel.selectMainFolder(folder) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$flag ${folder.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // + New Main Folder Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldTheme.extended.surfaceTier2)
                            .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .bouncyClickable { showNewMainDialog = true }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EmeraldPalette.EmeraldGlow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ New", fontSize = 12.sp, color = EmeraldPalette.EmeraldGlow, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Select Sub-Folder
            if (selectedMain != null) {
                Column {
                    Text(
                        text = "Sub-Folder (Optional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldTheme.extended.subText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val noneSelected = selectedSub == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (noneSelected) EmeraldPalette.DeepGreen else EmeraldTheme.extended.surfaceTier2)
                                .border(1.dp, if (noneSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder, RoundedCornerShape(10.dp))
                                .bouncyClickable { viewModel.selectSubFolder(null) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "None",
                                fontSize = 12.sp,
                                fontWeight = if (noneSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (noneSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        for (sub in subFolders) {
                            val isSelected = selectedSub?.id == sub.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) EmeraldPalette.DeepGreen else EmeraldTheme.extended.surfaceTier2)
                                    .border(1.dp, if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder, RoundedCornerShape(10.dp))
                                    .bouncyClickable { viewModel.selectSubFolder(sub) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = sub.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // + New Sub-Folder Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldTheme.extended.surfaceTier2)
                                .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .bouncyClickable { showNewSubDialog = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EmeraldPalette.EmeraldGlow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Sub-Folder", fontSize = 12.sp, color = EmeraldPalette.EmeraldGlow, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 2b. Sub-Sub Folder Selector (Tier 3)
                if (selectedSub != null) {
                    Column {
                        Text(
                            text = "3. Sub-Sub Folder (Optional in ${selectedSub?.name})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldTheme.extended.subText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val noneSubSubSelected = selectedSubSub == null
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (noneSubSubSelected) EmeraldPalette.DeepGreen else EmeraldTheme.extended.surfaceTier2)
                                    .border(1.dp, if (noneSubSubSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder, RoundedCornerShape(10.dp))
                                    .bouncyClickable { viewModel.selectSubSubFolder(null) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "None",
                                    fontSize = 12.sp,
                                    fontWeight = if (noneSubSubSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (noneSubSubSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            for (subSub in subSubFolders) {
                                val isSelected = selectedSubSub?.id == subSub.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) EmeraldPalette.DeepGreen else EmeraldTheme.extended.surfaceTier2)
                                        .border(1.dp, if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder, RoundedCornerShape(10.dp))
                                        .bouncyClickable { viewModel.selectSubSubFolder(subSub) }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = subSub.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // + New Sub-Sub Folder Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldTheme.extended.surfaceTier2)
                                    .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .bouncyClickable { showNewSubSubDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EmeraldPalette.EmeraldGlow, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Sub-Sub", fontSize = 12.sp, color = EmeraldPalette.EmeraldGlow, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Article / Gender Quick Selector
            Column {
                Text(
                    text = "Article / Part of Speech (e.g. German)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldTheme.extended.subText
                )
                val sourceLang = selectedMain?.sourceLanguage?.lowercase() ?: "de"
                val langName = LanguageRegistry.findByCode(sourceLang)?.name ?: "Word"
                Text(
                    text = "Article / Category ($langName)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldTheme.extended.subText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val articles = when (sourceLang) {
                        "de" -> listOf("der", "die", "das", "verb", "adj")
                        "es" -> listOf("el", "la", "un", "una", "verb", "adj")
                        "fr" -> listOf("le", "la", "un", "une", "verb", "adj")
                        "it" -> listOf("il", "la", "lo", "un", "verb", "adj")
                        "pt" -> listOf("o", "a", "um", "uma", "verb", "adj")
                        else -> listOf("noun", "verb", "adj", "adv", "phrase")
                    }
                    for (art in articles) {
                        val isSelected = article == art
                        val artColor = when (art) {
                            "der", "el", "le", "il", "o" -> EmeraldPalette.ArticleDer
                            "die", "la", "a" -> EmeraldPalette.ArticleDie
                            "das", "lo" -> EmeraldPalette.ArticleDas
                            else -> EmeraldPalette.SoftEmerald
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) artColor.copy(alpha = 0.25f) else EmeraldTheme.extended.surfaceTier2)
                                .border(1.dp, if (isSelected) artColor else EmeraldTheme.extended.glassBorder, RoundedCornerShape(8.dp))
                                .bouncyClickable { viewModel.selectArticle(art) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = art,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) artColor else EmeraldTheme.extended.subText
                            )
                        }
                    }
                }
            }

            // 4. Original Word Field
            OutlinedTextField(
                value = origWord,
                onValueChange = { viewModel.originalWord.value = it },
                label = { Text("Original Word *") },
                placeholder = { Text("e.g. das Geheimnis") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    unfocusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    focusedBorderColor = EmeraldPalette.EmeraldGlow,
                    unfocusedBorderColor = EmeraldTheme.extended.glassBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Translated Word Field
            OutlinedTextField(
                value = transWord,
                onValueChange = { viewModel.translatedWord.value = it },
                label = { Text("Translation *") },
                placeholder = { Text("e.g. the secret") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    unfocusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    focusedBorderColor = EmeraldPalette.EmeraldGlow,
                    unfocusedBorderColor = EmeraldTheme.extended.glassBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 6. Example Sentence Field
            OutlinedTextField(
                value = example,
                onValueChange = { viewModel.exampleSentence.value = it },
                label = { Text("Example Sentence (Optional)") },
                placeholder = { Text("e.g. Es ist ein großes Geheimnis.") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    unfocusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    focusedBorderColor = EmeraldPalette.EmeraldGlow,
                    unfocusedBorderColor = EmeraldTheme.extended.glassBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 7. Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes (Optional)") },
                placeholder = { Text("e.g. Plural: die Geheimnisse") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    unfocusedContainerColor = EmeraldTheme.extended.surfaceTier2,
                    focusedBorderColor = EmeraldPalette.EmeraldGlow,
                    unfocusedBorderColor = EmeraldTheme.extended.glassBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 8. Save Button
            Button(
                onClick = { viewModel.saveVocabulary() },
                enabled = origWord.isNotBlank() && transWord.isNotBlank() && selectedMain != null,
                shape = AppShapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bouncyClickable(onClick = { viewModel.saveVocabulary() })
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Save Word",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
