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

    // Brand Palette
    val saathiBlue = Color(0xFF1565C0)
    val rakshaGold = Color(0xFFFFD700)
    val lakshmanOrange = Color(0xFFFF9800)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. SAATHI
            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "साथी" else "Saathi",
                icon = Icons.Rounded.GppGood,
                selected = AppState.protectionMode == ProtectionMode.SAATHI,
                selectedColor = saathiBlue,
                onClick = { pendingMode = ProtectionMode.SAATHI }
            )

            // 2. RAKSHA
            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "रक्षा" else "Raksha",
                icon = Icons.Rounded.GppMaybe,
                selected = AppState.protectionMode == ProtectionMode.RAKSHA,
                selectedColor = rakshaGold,
                onClick = { pendingMode = ProtectionMode.RAKSHA }
            )

            // 3. LAKSHMAN
            ModeTile(
                modifier = Modifier.weight(1f),
                label = if (isHindi) "लक्ष्मण" else "Lakshman",
                icon = Icons.Rounded.Shield,
                selected = AppState.protectionMode == ProtectionMode.LAKSHMAN,
                selectedColor = lakshmanOrange,
                onClick = { pendingMode = ProtectionMode.LAKSHMAN }
            )
        }
    }

    // --- ENHANCED CONFIRMATION DIALOG ---
    if (pendingMode != null) {
        val mode = pendingMode!!

        // Define high-contrast colors for the dialog buttons
        val dialogAccentColor = when(mode) {
            ProtectionMode.SAATHI -> saathiBlue
            ProtectionMode.RAKSHA -> Color(0xFFDAA520) // Deep Goldenrod
            ProtectionMode.LAKSHMAN -> lakshmanOrange
            else -> Color.Gray
        }

        AlertDialog(
            onDismissRequest = { pendingMode = null },
            shape = RoundedCornerShape(32.dp),
            containerColor = Color(0xFFFDFDFD), // Clean off-white
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(dialogAccentColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(mode) {
                            ProtectionMode.SAATHI -> Icons.Rounded.GppGood
                            ProtectionMode.RAKSHA -> Icons.Rounded.GppMaybe
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
                    color = Color(0xFF1A1C1E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = getModeDescription(mode, isHindi),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF44474E),
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dialogAccentColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = if (isHindi) "बदलें" else "Confirm Switch",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (mode == ProtectionMode.RAKSHA) Color.Black else Color.White
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
                        color = Color.Gray,
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
            if (isHindi) "रक्षा मोड संदिग्ध ऐप्स और कॉल्स पर कड़ी निगरानी रखता है।"
            else "Raksha mode monitors suspicious apps and alerts your family."
        ProtectionMode.LAKSHMAN ->
            if (isHindi) "लक्ष्मण मोड पूर्ण सुरक्षा है - यह खतरों को तुरंत रोकता है।"
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
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(28.dp),
        color = if (selected) selectedColor else Color.White.copy(alpha = 0.12f),
        tonalElevation = if (selected) 12.dp else 0.dp,
        border = if (selected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = if (selected) {
                    if (selectedColor == Color(0xFFFFD700)) Color.Black else Color.White
                } else Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (selected) {
                        if (selectedColor == Color(0xFFFFD700)) Color.Black else Color.White
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
    ThreatLogger.logSystem("Perimeter updated: ${mode.name}")
}