package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.ThreatActionHandler
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun ThreatActionRow(
    threat: Threat,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isHindi = LanguageManager.isHindi()

    // Using your Brand Colors for specific actions
    val callBlue = Color(0xFF64B5F6)
    val blockRed = Color(0xFFEF5350)
    val verifyGreen = Color(0xFF81C784)
    val reportOrange = Color(0xFFFFB74D)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 1. CALL FAMILY
        BrandedActionItem(
            label = if (isHindi) "कॉल करें" else "Call Family",
            icon = Icons.Rounded.PhoneInTalk,
            color = callBlue,
            onClick = { ThreatActionHandler.callTrustedContact(context) }
        )

        // 2. BLOCK SOURCE
        BrandedActionItem(
            label = if (isHindi) "ब्लॉक करें" else "Block Scam",
            icon = Icons.Rounded.Block,
            color = blockRed,
            onClick = { ThreatActionHandler.blockSource(context, threat) }
        )

        // 3. VERIFY SOURCE
        BrandedActionItem(
            label = if (isHindi) "जांच करें" else "Verify",
            icon = Icons.Rounded.VerifiedUser,
            color = verifyGreen,
            onClick = { ThreatActionHandler.verifyMerchant(context) }
        )

        // 4. REPORT SCAM
        BrandedActionItem(
            label = if (isHindi) "रिपोर्ट" else "Report",
            icon = Icons.Rounded.Flag,
            color = reportOrange,
            onClick = { ThreatActionHandler.reportScam(threat) }
        )
    }
}

@Composable
private fun BrandedActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // High-Contrast Glass-morphism Button
        Surface(
            onClick = onClick,
            modifier = Modifier.size(64.dp), // Larger touch target for seniors
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.15f), // Glass effect
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Subtle Glow behind icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White, // All icons white to match brand
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Clean White Label
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewThreatActionRow() {
    val mockThreat = Threat(
        level = ThreatLevel.RISKY,
        score = 80,
        reasons = listOf("Mock"),
        sourceNumber = "123",
        sourceApp = "SMS"
    )

    // Previewing on the Brand Blue background
    Box(
        modifier = Modifier
            .background(Color(0xFF0D47A1))
            .padding(20.dp)
    ) {
        ThreatActionRow(threat = mockThreat)
    }
}