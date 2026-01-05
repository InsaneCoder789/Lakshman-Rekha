package com.lakshmanrekha.protect.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.GppMaybe
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.ProtectionNotifier
import com.lakshmanrekha.protect.utils.*

@Composable
fun ProtectionModeSelector(context: Context) {
    val isHindi = LanguageManager.isHindi()

    // State to track if the dialog should be visible
    var pendingMode by remember { mutableStateOf<ProtectionMode?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = if (isHindi) "सुरक्षा स्तर बदलें" else "Change Protection Level",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "साथी" else "Saathi",
                icon = Icons.Rounded.GppGood,
                selected = AppState.protectionMode == ProtectionMode.SAATHI,
                selectedColor = Color(0xFFFBBC04),
                onClick = { pendingMode = ProtectionMode.SAATHI } // Show Dialog
            )

            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "लक्ष्मण" else "Lakshman",
                icon = Icons.Rounded.Shield,
                selected = AppState.protectionMode == ProtectionMode.LAKSHMAN,
                selectedColor = Color(0xFF34A853),
                onClick = { pendingMode = ProtectionMode.LAKSHMAN } // Show Dialog
            )

            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "रक्षा" else "Raksha",
                icon = Icons.Rounded.GppMaybe,
                selected = AppState.protectionMode == ProtectionMode.RAKSHA,
                selectedColor = Color(0xFF1A73E8),
                onClick = { pendingMode = ProtectionMode.RAKSHA } // Show Dialog
            )
        }
    }

    // 🛡️ THE CONFIRMATION DIALOG
    if (pendingMode != null) {
        val mode = pendingMode!!
        AlertDialog(
            onDismissRequest = { pendingMode = null },
            shape = RoundedCornerShape(28.dp),
            icon = { Icon(Icons.Rounded.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = {
                Text(
                    text = if (isHindi) "${mode.name} मोड चुनें?" else "Switch to ${mode.name}?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = getModeDescription(mode, isHindi),
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        changeMode(context, mode)
                        pendingMode = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isHindi) "हाँ, बदलें" else "Yes, Change", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingMode = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", fontSize = 16.sp)
                }
            }
        )
    }
}

// Helper to explain what each mode does
private fun getModeDescription(mode: ProtectionMode, isHindi: Boolean): String {
    return when (mode) {
        ProtectionMode.SAATHI -> if (isHindi) "यह मोड आपको केवल चेतावनी देगा।" else "This mode will only give you silent guidance!"
        ProtectionMode.LAKSHMAN -> if (isHindi) "यह मोड संदिग्ध कॉल और ऐप्स को ब्लॉक करेगा।" else "This mode will gently intervene and protect!"
        ProtectionMode.RAKSHA -> if (isHindi) "पूरी सुरक्षा! यह मोड सभी अनजान संपर्कों को ब्लॉक कर देगा।" else "Maximum Safety! This gives you Strong Protections"
        else -> ""
    }
}

@Composable
private fun ModeTile(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) selectedColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = if (selected) 8.dp else 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun changeMode(context: Context, mode: ProtectionMode) {
    if (AppState.protectionMode == mode) return
    AppState.protectionMode = mode
    AppPrefs.updateMode(context, mode)
    ProtectionNotifier.show(context, mode)
    ThreatLogger.logSystem("Protection mode changed to ${mode.name}")
}