package com.sahed.my_own_vocabulary.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import com.sahed.my_own_vocabulary.BuildConfig
import com.sahed.my_own_vocabulary.R
import com.sahed.my_own_vocabulary.data.preferences.ThemeMode
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.components.gentleEntrance
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme
import com.sahed.my_own_vocabulary.util.DateUtils
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
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val defaultSourceLang by viewModel.defaultSourceLang.collectAsStateWithLifecycle()
    val defaultTargetLang by viewModel.defaultTargetLang.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val versionName = BuildConfig.VERSION_NAME

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showOtherAppsDialog by remember { mutableStateOf(false) }

    // Dialog: Language Pair Popup
    if (showLanguageDialog) {
        val popularPresets = listOf(
            "de" to "en",
            "es" to "en",
            "fr" to "en",
            "it" to "en",
            "ja" to "en",
            "bn" to "en",
            "ar" to "en"
        )
        val allPairs = if (popularPresets.none { it.first.equals(defaultSourceLang, ignoreCase = true) && it.second.equals(defaultTargetLang, ignoreCase = true) }) {
            listOf(defaultSourceLang to defaultTargetLang) + popularPresets
        } else {
            popularPresets
        }

        var showCustomInput by remember { mutableStateOf(false) }
        var customSource by remember(defaultSourceLang) { mutableStateOf(defaultSourceLang) }
        var customTarget by remember(defaultTargetLang) { mutableStateOf(defaultTargetLang) }

        EmeraldAlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = "Default Language Pair",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for ((src, tgt) in allPairs) {
                        val isSelected = defaultSourceLang.equals(src, ignoreCase = true) && defaultTargetLang.equals(tgt, ignoreCase = true)
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
                                    viewModel.setDefaultLanguages(src, tgt)
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${getLangDisplayName(src)} ➔ ${getLangDisplayName(tgt)}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) EmeraldPalette.EmeraldGlow else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${src.uppercase()} ➔ ${tgt.uppercase()}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = EmeraldPalette.SubTextGrey
                                )
                            }
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

                    // Option for custom code pair
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldTheme.extended.surfaceTier2)
                            .border(1.dp, EmeraldTheme.extended.glassBorder, RoundedCornerShape(12.dp))
                            .bouncyClickable { showCustomInput = !showCustomInput }
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Language Pair...",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (showCustomInput) "▲" else "▼",
                            fontSize = 12.sp,
                            color = EmeraldPalette.SubTextGrey
                        )
                    }

                    if (showCustomInput) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = customSource,
                                onValueChange = { customSource = it },
                                label = { Text("Source Code") },
                                placeholder = { Text("e.g. de") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = customTarget,
                                onValueChange = { customTarget = it },
                                label = { Text("Target Code") },
                                placeholder = { Text("e.g. en") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                if (customSource.isNotBlank() && customTarget.isNotBlank()) {
                                    viewModel.setDefaultLanguages(customSource.trim().lowercase(), customTarget.trim().lowercase())
                                    showLanguageDialog = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPalette.SoftEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Text("Save Custom Pair", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            },
            confirmButtonText = "Done",
            dismissButtonText = null,
            onConfirm = { showLanguageDialog = false }
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

    // Dialog: Other Android Apps Popup
    if (showOtherAppsDialog) {
        Dialog(
            onDismissRequest = { showOtherAppsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF13241F),
                border = BorderStroke(1.dp, Color(0xFF223E34).copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header: Icon + Title + Close Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color(0xFF1D3B33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = EmeraldPalette.EmeraldGlow,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Other Android Apps",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E332B))
                                .bouncyClickable { showOtherAppsDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Make your life more productive and organized by using my other ad-free apps.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        ),
                        color = EmeraldPalette.SubTextGrey
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Salah Tracker App Card
                    OtherAppCard(
                        logoRes = R.drawable.ic_salah_tracker,
                        title = "Salah Tracker App",
                        subtitle = "By Sahed Alom Sumit",
                        badge = "Free",
                        description = "Track Salah, log prayers, and see your performance over time without ads.",
                        onPlayStoreClick = {
                            openGooglePlayApp(
                                context = context,
                                packageName = "com.sahed.salah_tracker",
                                webUrl = "https://play.google.com/store/apps/details?id=com.sahed.salah_tracker"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Money Tracker App Card
                    OtherAppCard(
                        logoRes = R.drawable.ic_money_tracker,
                        title = "Money Tracker App",
                        subtitle = "By Sahed Alom Sumit",
                        badge = "Free",
                        description = "Track money, log income & see breakdown and savings grow over time without ads.",
                        onPlayStoreClick = {
                            openGooglePlayApp(
                                context = context,
                                packageName = "com.sahed.money_tracker",
                                webUrl = "https://play.google.com/store/apps/details?id=com.sahed.money_tracker"
                            )
                        }
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
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
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Profile Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF19382C),
                    border = BorderStroke(1.dp, Color(0xFF2D5C4B).copy(alpha = 0.85f)),
                    modifier = Modifier.fillMaxWidth()
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
                                    .size(60.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6B46C1)),
                                contentAlignment = Alignment.Center
                            ) {
                                val initial = (currentUser?.displayName?.firstOrNull()
                                    ?: currentUser?.email?.firstOrNull() ?: 'S').uppercaseChar()
                                Text(
                                    text = initial.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 24.sp
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
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = currentUser?.email ?: "sahedalomsumit@gmail.com",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = EmeraldPalette.SubTextGrey
                            )
                        }
                    }
                }
            }

            // Section: App Settings
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(title = "App Settings")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Language Pair
                        SettingsRowItem(
                            icon = Icons.Rounded.Language,
                            title = "Language",
                            subtitle = "${defaultSourceLang.uppercase()} ➔ ${defaultTargetLang.uppercase()} (${getLangDisplayName(defaultSourceLang)})",
                            onClick = { showLanguageDialog = true }
                        )

                        // 2. Theme
                        val themeSubtitle = when (currentTheme) {
                            ThemeMode.DARK -> "Dark"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.SYSTEM -> "System Default"
                        }
                        SettingsRowItem(
                            icon = Icons.Rounded.Palette,
                            title = "Theme",
                            subtitle = themeSubtitle,
                            onClick = { showThemeDialog = true }
                        )

                        // 3. Notifications
                        SettingsRowItem(
                            icon = Icons.Rounded.Notifications,
                            title = "Notifications",
                            subtitle = if (notificationsEnabled) "Europe/Helsinki (UTC+3)" else "Notifications paused",
                            trailingContent = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { checked ->
                                        viewModel.setNotificationsEnabled(checked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = EmeraldPalette.SoftEmerald,
                                        uncheckedThumbColor = EmeraldPalette.SubTextGrey,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            },
                            onClick = { viewModel.setNotificationsEnabled(!notificationsEnabled) }
                        )

                        // 4. Manage Sources
                        SettingsRowItem(
                            icon = Icons.Rounded.FolderOpen,
                            title = "Manage Sources",
                            subtitle = "Main, sub & sub-sub folders",
                            onClick = onNavigateToManageFolders
                        )
                    }
                }
            }

            // Section: Account
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(title = "Account")

                    SettingsRowItem(
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        iconColor = Color(0xFFEF5350),
                        title = "Sign Out",
                        subtitle = "Disconnect from Cloud",
                        onClick = { showSignOutDialog = true }
                    )
                }
            }

            // 4. Support Section (Optional App-support Donation)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(title = "Support & Other Apps", modifier = Modifier.gentleEntrance(4))

                    SettingsRowItem(
                        icon = Icons.Default.Apps,
                        title = "Other Android Apps",
                        subtitle = "By Sahed Alom Sumit",
                        onClick = { showOtherAppsDialog = true }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF13241F),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFF223E34).copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .gentleEntrance(4)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "If you benefit from this ad-free app, you may support it for maintenance.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.stripe.com/7sY9AS57S4XL7F4aqP8AE03"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Donate with Stripe", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 5. Footer
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gentleEntrance(5),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version $versionName",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "© ${DateUtils.getCurrentYear()} Sahed Alom Sumit",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val infiniteHeartTransition = rememberInfiniteTransition(label = "heartPulse")
                    val heartScale by infiniteHeartTransition.animateFloat(
                        initialValue = 0.82f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "heartScale"
                    )

                    Row(
                        modifier = Modifier
                            .bouncyClickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sahedalomsumit.com"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Built with ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "❤️",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = heartScale
                                    scaleY = heartScale
                                }
                                .padding(horizontal = 2.dp)
                        )
                        Text(
                            text = " by ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Sahed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF8719)
                        )
                    }
                    Spacer(modifier = Modifier.height(70.dp))
                }
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
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = EmeraldPalette.SubTextGrey
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = EmeraldPalette.SubTextGrey.copy(alpha = 0.7f),
                modifier = Modifier.size(15.dp)
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
        "zh" -> "Chinese"
        "ru" -> "Russian"
        "pt" -> "Portuguese"
        "tr" -> "Turkish"
        "hi" -> "Hindi"
        "ko" -> "Korean"
        "nl" -> "Dutch"
        "sv" -> "Swedish"
        "fi" -> "Finnish"
        else -> code.uppercase()
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        ),
        color = EmeraldPalette.EmeraldGlow,
        modifier = modifier.padding(start = 2.dp, top = 6.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
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
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(EmeraldPalette.Surface2Dark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPalette.EmeraldGlow,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = EmeraldPalette.SubTextGrey
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = EmeraldPalette.SubTextGrey.copy(alpha = 0.7f),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun OtherAppCard(
    logoRes: Int,
    title: String,
    subtitle: String,
    badge: String,
    description: String,
    onPlayStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0F1E19),
        border = BorderStroke(1.dp, Color(0xFF1E3A31)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // App Logo + Title + Badge + Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color.White,
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF143B31))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPalette.EmeraldGlow
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                        color = EmeraldPalette.SubTextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlayStoreClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPalette.SoftEmerald
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View on Google Play",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun openGooglePlayApp(context: android.content.Context, packageName: String, webUrl: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sahedalomsumit.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallbackIntent)
        }
    }
}
