package com.sahed.my_own_vocabulary.ui.screens.login

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahed.my_own_vocabulary.R
import com.sahed.my_own_vocabulary.ui.components.BuiltBySahedFooter
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Auto-navigate if already signed in or on sign-in event
    LaunchedEffect(Unit) {
        viewModel.loginSuccessEvent.collect {
            onNavigateToHome()
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            showErrorDialog = true
        }
    }

    if (showErrorDialog && uiState.errorMessage != null) {
        EmeraldAlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = "Sign-In Notice",
            content = {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = EmeraldTheme.extended.subText,
                    fontSize = 14.sp
                )
            },
            confirmButtonText = "OK",
            onConfirm = {
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background ambient emerald glow aura
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EmeraldPalette.SoftEmerald.copy(alpha = 0.18f),
                                EmeraldPalette.SoftEmerald.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Brand Identity & Highlights
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // App Emblem with glowing layered ring
                    Image(
                        painter = painterResource(id = R.drawable.app_logo_tactile),
                        contentDescription = "My Own Vocabulary Logo",
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = EmeraldPalette.EmeraldGlow,
                                spotColor = EmeraldPalette.EmeraldGlow
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.5.dp, EmeraldPalette.EmeraldGlow.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // App Title
                    Text(
                        text = "My Own Vocabulary",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle / Tagline
                    Text(
                        text = "Master your language journey, one word at a time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldTheme.extended.subText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Feature highlights cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FeatureHighlightRow(
                            icon = Icons.Rounded.AutoAwesome,
                            title = "Smart Spaced Repetition",
                            description = "Strengthen vocabulary with tailored memory retention"
                        )
                        FeatureHighlightRow(
                            icon = Icons.Rounded.RecordVoiceOver,
                            title = "Instant Native Audio",
                            description = "Flawless pronunciation tuned to your speech pace"
                        )
                        FeatureHighlightRow(
                            icon = Icons.Rounded.CloudSync,
                            title = "Cloud Sync & Backup",
                            description = "Your personal dictionary safe across all devices"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom section: Sign-In Action Area
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Sign-In Button ("Continue with Google")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFFF7FAF8)
                                    )
                                )
                            )
                            .border(1.dp, Color(0x332E9C7E), RoundedCornerShape(14.dp))
                            .bouncyClickable {
                                if (!uiState.isLoading) {
                                    val activity = context.findActivity()
                                    if (activity != null) {
                                        viewModel.signInWithGoogle(activity)
                                    } else {
                                        Toast.makeText(context, "Cannot find current Activity", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp,
                                    color = EmeraldPalette.SoftEmerald
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Connecting...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF1F2937)
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Google Logo",
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color(0xFF1F2937)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Option: Continue as Guest / Offline exploration
                    TextButton(
                        onClick = { viewModel.continueAsGuest() },
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Explore as Guest / Offline",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = EmeraldPalette.EmeraldGlow
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Footer branding
                    BuiltBySahedFooter()
                }
            }
        }
    }
}

@Composable
private fun FeatureHighlightRow(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    EmeraldGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPalette.EmeraldGlow,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldTheme.extended.subText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Traverses Context wrapper tree to retrieve the host Activity for Credential Manager.
 */
private fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
