package com.lakshmanrekha.protect.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.core.ThreatActionHandler
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun OneTapActions(
    context: Context,
    threat: Threat,
    onDismiss: (() -> Unit)? = null
) {
    val isHindi = LanguageManager.isHindi()

    // Brand Colors
    val cardSurface = Color(0xFF1565C0) // Branded card blue
    val brandRed = Color(0xFFEF5350)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded Drag Handle
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (isHindi) "तुरंत सहायता लें" else "Quick Assistance",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )

            Spacer(Modifier.height(24.dp))

            // 2x2 Grid using Branded Glass-Morphism
            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.PhoneInTalk,
                    title = if (isHindi) "परिवार" else "Call Family",
                    subtitle = if (isHindi) "मदद मांगें" else "Ask for help",
                    color = Color(0xFF64B5F6), // Brighter blue for contrast
                    onClick = {
                        ThreatActionHandler.callTrustedContact(context)
                        onDismiss?.invoke()
                    }
                )
                Spacer(Modifier.width(16.dp))
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Block,
                    title = if (isHindi) "ब्लॉक" else "Block",
                    subtitle = if (isHindi) "इसे रोकें" else "Stop source",
                    color = Color(0xFFFFA726), // Brighter orange
                    onClick = {
                        ThreatActionHandler.blockSource(context, threat)
                        onDismiss?.invoke()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.VerifiedUser,
                    title = if (isHindi) "जांचें" else "Verify",
                    subtitle = if (isHindi) "सत्यापित करें" else "Check merchant",
                    color = Color(0xFFBA68C8), // Soft purple
                    onClick = {
                        ThreatActionHandler.verifyMerchant(context)
                        onDismiss?.invoke()
                    }
                )
                Spacer(Modifier.width(16.dp))
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.School,
                    title = if (isHindi) "सीखें" else "Coach",
                    subtitle = if (isHindi) "टिप्स लें" else "Get safety tips",
                    color = Color(0xFF81C784), // Brighter green
                    onClick = {
                        CoachLauncher.launch(context)
                        onDismiss?.invoke()
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // High-Contrast Report Action
            Button(
                onClick = {
                    ThreatActionHandler.reportScam(threat)
                    onDismiss?.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandRed.copy(alpha = 0.2f),
                    contentColor = brandRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, brandRed.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Rounded.GppMaybe, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (isHindi) "धोखाधड़ी की रिपोर्ट करें" else "Report this Scam",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.08f), // Subtle glass effect
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// --- PREVIEW ---
@Preview
@Composable
fun PreviewOneTap() {
    val mockThreat = Threat(ThreatLevel.RISKY, 0, emptyList(), "123", "App")
    Box(Modifier.background(Color(0xFF0D47A1)).padding(top = 100.dp)) {
        OneTapActions(context = androidx.compose.ui.platform.LocalContext.current, threat = mockThreat)
    }
}