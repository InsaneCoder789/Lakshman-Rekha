package com.lakshmanrekha.protect.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.ProtectionNotifier
import com.lakshmanrekha.protect.utils.*

@Composable
fun ProtectionModeSelector(context: Context) {
    val isHindi = LanguageManager.isHindi()
    var pendingMode by remember { mutableStateOf<ProtectionMode?>(null) }

    // --- NEW BRAND PALETTE ---
    val saathiBlue = Color(0xFF157EFA)
    val rakshaGold = Color(0xFFFEAB01)
    val lakshmanRed = Color(0xFFFE4015)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "साथी" else "Saathi",
                icon = Icons.Rounded.Handshake,
                selected = AppState.protectionMode == ProtectionMode.SAATHI,
                selectedColor = saathiBlue,
                onClick = { pendingMode = ProtectionMode.SAATHI }
            )

            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "रक्षा" else "Raksha",
                icon = Icons.Rounded.GppGood,
                selected = AppState.protectionMode == ProtectionMode.RAKSHA,
                selectedColor = rakshaGold,
                onClick = { pendingMode = ProtectionMode.RAKSHA }
            )

            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "लक्ष्मण" else "Lakshman",
                icon = Icons.Rounded.Shield,
                selected = AppState.protectionMode == ProtectionMode.LAKSHMAN,
                selectedColor = lakshmanRed,
                onClick = { pendingMode = ProtectionMode.LAKSHMAN }
            )
        }
    }

    if (pendingMode != null) {
        val mode = pendingMode!!
        val dialogAccentColor = when(mode) {
            ProtectionMode.SAATHI -> saathiBlue
            ProtectionMode.RAKSHA -> rakshaGold
            ProtectionMode.LAKSHMAN -> lakshmanRed
            else -> Color.Gray
        }

        AlertDialog(
            onDismissRequest = { pendingMode = null },
            shape = RoundedCornerShape(32.dp),
            containerColor = Color(0xFF1A212B),
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(dialogAccentColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(mode) {
                            ProtectionMode.SAATHI -> Icons.Rounded.Handshake
                            ProtectionMode.RAKSHA -> Icons.Rounded.GppGood
                            else -> Icons.Rounded.Shield
                        },
                        contentDescription = null,
                        tint = dialogAccentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (isHindi) "${mode.name} चालू करें?" else "Activate ${mode.name}?",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = getModeDescription(mode, isHindi),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        changeMode(context, mode)
                        pendingMode = null
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = dialogAccentColor,
                        // Using Black content color for Gold, White for others
                        contentColor = if (mode == ProtectionMode.RAKSHA) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isHindi) "हाँ, मोड बदलें" else "Confirm Switch",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingMode = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}

private fun getModeDescription(mode: ProtectionMode, isHindi: Boolean): String {
    return when (mode) {
        ProtectionMode.SAATHI ->
            if (isHindi) "साथी मोड केवल चेतावनी देता है और आपको शिक्षित करता है।"
            else "Saathi mode provides educational alerts without blocking."
        ProtectionMode.RAKSHA ->
            if (isHindi) "रक्षा मोड संदिग्ध ऐप्स पर कड़ी निगरानी रखता है और परिवार को सूचित करता है।"
            else "Raksha mode monitors suspicious apps and alerts your family."
        ProtectionMode.LAKSHMAN ->
            if (isHindi) "लक्ष्मण मोड पूर्ण लॉकडाउन है - यह सभी खतरों को तुरंत रोकता है।"
            else "Lakshman mode is full lockdown - it stops threats instantly."
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
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (selected) selectedColor else Color.White.copy(alpha = 0.12f),
        border = if (selected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
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
                tint = if (selected) {
                    // Optimized contrast: Black for Raksha (#FEAB01), White for Saathi and Lakshman
                    if (selectedColor == Color(0xFFFEAB01)) Color.Black else Color.White
                } else Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = if (selected) {
                        if (selectedColor == Color(0xFFFEAB01)) Color.Black else Color.White
                    } else Color.White.copy(alpha = 0.7f)
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
    ThreatLogger.logSystem("Security perimeter set to: ${mode.name}")
}