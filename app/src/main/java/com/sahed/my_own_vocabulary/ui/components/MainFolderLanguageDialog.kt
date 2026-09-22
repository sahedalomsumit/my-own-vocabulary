package com.sahed.my_own_vocabulary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.AppShapes
import com.sahed.my_own_vocabulary.ui.designsystem.theme.DialogShape
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme
import com.sahed.my_own_vocabulary.util.LanguageRegistry
import com.sahed.my_own_vocabulary.util.SupportedLanguage
import com.sahed.my_own_vocabulary.util.TtsManager

@Composable
fun MainFolderLanguageDialog(
    onDismissRequest: () -> Unit,
    initialName: String = "",
    initialSourceLang: String = "de",
    initialTargetLang: String = "en",
    isEditing: Boolean = false,
    ttsManager: TtsManager? = null,
    onConfirm: (name: String, sourceLang: String, targetLang: String) -> Unit
) {
    val initialLang = remember(initialSourceLang) {
        LanguageRegistry.findByCode(initialSourceLang) ?: LanguageRegistry.languages.first()
    }

    var selectedLang by remember { mutableStateOf(initialLang) }
    var folderName by remember {
        mutableStateOf(if (initialName.isNotBlank()) initialName else selectedLang.name)
    }
    var hasCustomizedName by remember { mutableStateOf(initialName.isNotBlank()) }
    var searchQuery by remember { mutableStateOf("") }
    var targetLang by remember { mutableStateOf(initialTargetLang) }

    val filteredLanguages = remember(searchQuery) {
        LanguageRegistry.search(searchQuery)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(DialogShape)
                .background(EmeraldTheme.extended.surfaceTier1)
                .border(1.2.dp, EmeraldTheme.extended.glassBorder, DialogShape)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Column {
                    Text(
                        text = if (isEditing) "Edit Main Folder" else "Create Main Folder",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Choose the language of the vocabulary words you are studying. Audio will follow this language.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldTheme.extended.subText
                    )
                }

                // Selected Language Hero Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLang.flagEmoji,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${selectedLang.name} (${selectedLang.nativeName})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Country: ${selectedLang.country} • Code: ${selectedLang.code.uppercase()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldPalette.EmeraldGlow
                                )
                            }
                        }

                        // Test Pronunciation
                        IconButton(
                            onClick = {
                                ttsManager?.speak(
                                    text = selectedLang.greetingSample,
                                    languageCode = selectedLang.code
                                )
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Test Pronunciation",
                                tint = EmeraldPalette.EmeraldGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Folder Name input
                OutlinedTextField(
                    value = folderName,
                    onValueChange = {
                        folderName = it
                        hasCustomizedName = true
                    },
                    label = { Text("Folder Name") },
                    placeholder = { Text("e.g. ${selectedLang.name}, ${selectedLang.name} B1") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPalette.EmeraldGlow,
                        unfocusedBorderColor = EmeraldTheme.extended.glassBorder
                    )
                )

                // Search by country or language name
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by country, language, code (e.g. Spain, de)...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = EmeraldTheme.extended.subText,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear",
                                    tint = EmeraldTheme.extended.subText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPalette.SoftEmerald,
                        unfocusedBorderColor = EmeraldTheme.extended.glassBorder.copy(alpha = 0.5f)
                    )
                )

                // Language List with flags
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldTheme.extended.surfaceTier2)
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredLanguages, key = { it.code }) { lang ->
                        val isSelected = lang.code == selectedLang.code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPalette.SoftEmerald.copy(alpha = 0.25f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPalette.EmeraldGlow.copy(alpha = 0.6f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .bouncyClickable {
                                    selectedLang = lang
                                    if (!hasCustomizedName) {
                                        folderName = lang.name
                                    }
                                    ttsManager?.speak(lang.greetingSample, lang.code)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = lang.flagEmoji,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = lang.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) EmeraldPalette.EmeraldGlow else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${lang.nativeName} • ${lang.country}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = EmeraldTheme.extended.subText
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldTheme.extended.surfaceTier3)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = lang.code.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldTheme.extended.subText
                                    )
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Selected",
                                        tint = EmeraldPalette.EmeraldGlow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        shape = AppShapes.small,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = EmeraldTheme.extended.subText)
                    }

                    Button(
                        onClick = {
                            if (folderName.isNotBlank()) {
                                onConfirm(folderName.trim(), selectedLang.code, targetLang)
                            }
                        },
                        enabled = folderName.isNotBlank(),
                        shape = AppShapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPalette.SoftEmerald,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isEditing) "Save Changes" else "Create Folder",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
