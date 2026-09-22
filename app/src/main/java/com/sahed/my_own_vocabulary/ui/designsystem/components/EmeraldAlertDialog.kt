package com.sahed.my_own_vocabulary.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sahed.my_own_vocabulary.ui.designsystem.theme.AppShapes
import com.sahed.my_own_vocabulary.ui.designsystem.theme.DialogShape
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme

@Composable
fun EmeraldAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    confirmButtonText: String = "Confirm",
    onConfirm: () -> Unit,
    dismissButtonText: String? = "Cancel",
    isDestructive: Boolean = false,
    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DialogShape)
                .background(EmeraldTheme.extended.surfaceTier1)
                .border(1.2.dp, EmeraldTheme.extended.glassBorder, DialogShape)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissButtonText != null) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            shape = AppShapes.small,
                            modifier = Modifier
                                .weight(1f)
                                .bouncyClickable(onClick = onDismissRequest)
                        ) {
                            Text(text = dismissButtonText, color = EmeraldTheme.extended.subText)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = onConfirm,
                        shape = AppShapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDestructive) EmeraldPalette.ErrorRed else EmeraldPalette.SoftEmerald,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .bouncyClickable(onClick = onConfirm)
                    ) {
                        Text(text = confirmButtonText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
