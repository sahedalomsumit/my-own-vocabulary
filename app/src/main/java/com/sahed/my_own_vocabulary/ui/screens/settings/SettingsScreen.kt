package com.sahed.my_own_vocabulary.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sahed.my_own_vocabulary.data.preferences.ThemeMode
import com.sahed.my_own_vocabulary.ui.components.BuiltBySahedFooter
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToManageFolders: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()
    val speechPitch by viewModel.speechPitch.collectAsStateWithLifecycle()
    val autoPronounceSave by viewModel.autoPronounceOnSave.collectAsStateWithLifecycle()
    val autoPronounceQuiz by viewModel.autoPronounceInQuiz.collectAsStateWithLifecycle()
    val defaultSourceLang by viewModel.defaultSourceLang.collectAsStateWithLifecycle()
    val defaultTargetLang by viewModel.defaultTargetLang.collectAsStateWithLifecycle()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Dialog: Language Pair Popup
    if (showLanguageDialog) {
        var inputSource by remember(defaultSourceLang) { mutableStateOf(defaultSourceLang) }
        var inputTarget by remember(defaultTargetLang) { mutableStateOf(defaultTargetLang) }
        val presets = listOf(
            "de" to "en",
            "es" to "en",
            "fr" to "en",
            "it" to "en",
            "ja" to "en"
        )

        EmeraldAlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = "Default Language Pair",
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Choose your primary study language pair. This will be used as the default for new folders and pronunciation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldTheme.extended.subText
                    )

                    // Quick presets
                    Text(
                        text = "Popular Presets",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPalette.EmeraldGlow
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((src, tgt) in presets) {
                            val isSelected = inputSource.equals(src, ignoreCase = true) && inputTarget.equals(tgt, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmeraldPalette.SoftEmerald else EmeraldTheme.extended.surfaceTier2)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .bouncyClickable {
                                        inputSource = src
                                        inputTarget = tgt
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${src.uppercase()} ➔ ${tgt.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else EmeraldTheme.extended.subText
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = inputSource,
                            onValueChange = { inputSource = it },
                            label = { Text("Source Code") },
                            placeholder = { Text("e.g. de") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputTarget,
                            onValueChange = { inputTarget = it },
                            label = { Text("Target Code") },
                            placeholder = { Text("e.g. en") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButtonText = "Save",
            onConfirm = {
                if (inputSource.isNotBlank() && inputTarget.isNotBlank()) {
                    viewModel.setDefaultLanguages(inputSource, inputTarget)
                    showLanguageDialog = false
                }
            }
        )
    }

    // Dialog: Theme Popup
    if (showThemeDialog) {
        EmeraldAlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = "Choose Theme",
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val themes = listOf(
                        ThemeMode.DARK to "Dark",
                        ThemeMode.LIGHT to "Light",
                        ThemeMode.SYSTEM to "System Default"
                    )
                    for ((mode, label) in themes) {
                        val isSelected = currentTheme == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldPalette.SoftEmerald.copy(alpha = 0.2f) else EmeraldTheme.extended.surfaceTier2)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPalette.EmeraldGlow else EmeraldTheme.extended.glassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .bouncyClickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) EmeraldPalette.EmeraldGlow else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = EmeraldPalette.EmeraldGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButtonText = "Done",
            onConfirm = { showThemeDialog = false }
        )
    }

    // Dialog: Voice & Speech Popup
    if (showVoiceDialog) {
        var localRate by remember(speechRate) { mutableFloatStateOf(speechRate) }
        var localPitch by remember(speechPitch) { mutableFloatStateOf(speechPitch) }

        EmeraldAlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = "Voice & Pronunciation",
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Speech Rate
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Speech Rate",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", localRate)}x",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPalette.EmeraldGlow
                            )
                        }
                        Slider(
                            value = localRate,
                            onValueChange = {
                                localRate = it
                                viewModel.setSpeechRate(it)
                            },
                            valueRange = 0.5f..1.5f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPalette.SoftEmerald,
                                activeTrackColor = EmeraldPalette.EmeraldGlow
                            )
                        )
                    }

                    // Pitch
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Voice Pitch",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", localPitch)}x",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPalette.EmeraldGlow
                            )
                        }
                        Slider(
                            value = localPitch,
                            onValueChange = {
                                localPitch = it
                                viewModel.setSpeechPitch(it)
                            },
                            valueRange = 0.5f..1.5f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPalette.SoftEmerald,
                                activeTrackColor = EmeraldPalette.EmeraldGlow
                            )
                        )
                    }

                    // Test Audio Button
                    Button(
                        onClick = { viewModel.testPronunciation() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.DeepGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .bouncyClickable { viewModel.testPronunciation() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = null,
                            tint = EmeraldPalette.EmeraldGlow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Voice Audio", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButtonText = "Done",
            onConfirm = { showVoiceDialog = false }
        )
    }

    // Dialog: Sign Out Confirmation Popup
    if (showSignOutDialog) {
        EmeraldAlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = "Sign Out?",
            content = {
                Text(
                    text = "Are you sure you want to disconnect from Cloud? Your local vocabulary data will remain safe on your device.",
                    color = EmeraldTheme.extended.subText
                )
            },
            confirmButtonText = "Sign Out",
            isDestructive = true,
            onConfirm = {
                showSignOutDialog = false
                viewModel.signOut()
                onSignOut()
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header: "Settings"
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // Profile Card
            item {
                EmeraldGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val photoUrl = currentUser?.photoUrl?.toString()
                        if (!photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, EmeraldPalette.EmeraldGlow, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPalette.SoftEmerald)
                                    .border(2.dp, EmeraldPalette.EmeraldGlow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val initial = (currentUser?.displayName?.firstOrNull()
                                    ?: currentUser?.email?.firstOrNull() ?: 'S').uppercaseChar()
                                Text(
                                    text = initial.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.displayName ?: "Sahed Alom Sumit",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentUser?.email ?: "sahedalomsumit@gmail.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldTheme.extended.subText
                            )
                        }
                    }
                }
            }

            // Section: App Settings
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = EmeraldPalette.EmeraldGlow,
                        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                    )

                    EmeraldGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            // 1. Language Pair (Popup)
                            SettingsRowItem(
                                icon = Icons.Rounded.Translate,
                                title = "Language Pair",
                                subtitle = "${defaultSourceLang.uppercase()} ➔ ${defaultTargetLang.uppercase()} (${getLangDisplayName(defaultSourceLang)})",
                                onClick = { showLanguageDialog = true }
                            )

                            // 2. Theme (Popup)
                            val themeSubtitle = when (currentTheme) {
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.SYSTEM -> "System"
                            }
                            SettingsRowItem(
                                icon = Icons.Rounded.Palette,
                                title = "Theme",
                                subtitle = themeSubtitle,
                                onClick = { showThemeDialog = true }
                            )

                            // 3. Auto-Pronounce (Switch)
                            val isAutoPronounceActive = autoPronounceSave || autoPronounceQuiz
                            SettingsRowItem(
                                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                                title = "Notifications & Speech",
                                subtitle = if (isAutoPronounceActive) "Auto-pronounce enabled" else "Audio on click only",
                                trailingContent = {
                                    Switch(
                                        checked = isAutoPronounceActive,
                                        onCheckedChange = { checked ->
                                            viewModel.setAutoPronounceAll(checked)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = EmeraldPalette.SoftEmerald,
                                            uncheckedThumbColor = EmeraldTheme.extended.subText,
                                            uncheckedTrackColor = EmeraldTheme.extended.surfaceTier2
                                        )
                                    )
                                }
                            )

                            // 4. Voice & Speech (Popup)
                            SettingsRowItem(
                                icon = Icons.Rounded.RecordVoiceOver,
                                title = "Voice Settings",
                                subtitle = "Speed ${String.format(Locale.US, "%.1f", speechRate)}x, Pitch ${String.format(Locale.US, "%.1f", speechPitch)}x",
                                onClick = { showVoiceDialog = true }
                            )

                            // 5. Manage Folders
                            SettingsRowItem(
                                icon = Icons.Rounded.FolderOpen,
                                title = "Manage Sources",
                                subtitle = "Main, sub & sub-sub folders",
                                onClick = onNavigateToManageFolders
                            )
                        }
                    }
                }
            }

            // Section: Account
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = EmeraldPalette.EmeraldGlow,
                        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                    )

                    EmeraldGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            SettingsRowItem(
                                icon = Icons.AutoMirrored.Rounded.Logout,
                                iconColor = Color(0xFFEF4444),
                                iconBgColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                                title = "Sign Out",
                                subtitle = "Disconnect from Cloud",
                                onClick = { showSignOutDialog = true }
                            )
                        }
                    }
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(10.dp))
                BuiltBySahedFooter()
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconColor: Color = EmeraldPalette.EmeraldGlow,
    iconBgColor: Color = EmeraldPalette.SoftEmerald.copy(alpha = 0.15f),
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.bouncyClickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(clickModifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = EmeraldTheme.extended.subText
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = EmeraldTheme.extended.subText.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

private fun getLangDisplayName(code: String): String {
    return when (code.lowercase()) {
        "de" -> "German"
        "en" -> "English"
        "es" -> "Spanish"
        "fr" -> "French"
        "it" -> "Italian"
        "ja" -> "Japanese"
        "bn" -> "Bengali"
        "ar" -> "Arabic"
        else -> code.uppercase()
    }
}
