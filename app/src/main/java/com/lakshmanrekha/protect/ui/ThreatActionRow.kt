package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.BorderStroke
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
    // 🚫 SAFETY RULE:
    // Only show actions for RISKY and DANGEROUS threats
    if (threat.level != ThreatLevel.RISKY &&
        threat.level != ThreatLevel.DANGEROUS
    ) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isHindi = LanguageManager.isHindi()

    // Brand colors
    val whatsappGreen = Color(0xFF25D366)
    val blockRed = Color(0xFFEF5350)
    val verifyGreen = Color(0xFF81C784)
    val reportOrange = Color(0xFFFFB74D)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {

        /* ---------------- HEADER ---------------- */
        Text(
            text =
                if (isHindi)
                    "तुरंत कार्रवाई करें"
                else
                    "Immediate Actions",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
        )

        Text(
            text =
                if (isHindi)
                    "यह खतरा गंभीर हो सकता है"
                else
                    "This threat may cause harm",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )

        /* ---------------- ACTIONS ---------------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // 1. VERIFY VIA WHATSAPP (🆕 REPLACED CALL FAMILY WITH WHATSAPP)
            BrandedActionItem(
                label = if (isHindi) "WhatsApp जांच" else "Ask Family",
                icon = Icons.Rounded.Share,
                color = whatsappGreen,
                onClick = {
                    ThreatActionHandler.verifyViaWhatsApp(context, threat)
                }
            )

            // 2. BLOCK SOURCE
            BrandedActionItem(
                label = if (isHindi) "ब्लॉक करें" else "Block",
                icon = Icons.Rounded.Block,
                color = blockRed,
                onClick = {
                    ThreatActionHandler.blockSource(context, threat)
                }
            )

            // 3. VERIFY SOURCE
            BrandedActionItem(
                label = if (isHindi) "जांच करें" else "Verify",
                icon = Icons.Rounded.VerifiedUser,
                color = verifyGreen,
                onClick = {
                    ThreatActionHandler.verifyMerchant(context)
                }
            )

            // 4. REPORT SCAM
            BrandedActionItem(
                label = if (isHindi) "रिपोर्ट" else "Report",
                icon = Icons.Rounded.Flag,
                color = reportOrange,
                onClick = {
                    ThreatActionHandler.reportScam(threat)
                }
            )
        }

        /* ---------------- SEPARATOR ---------------- */
        Spacer(Modifier.height(8.dp))
        Divider(
            color = Color.White.copy(alpha = 0.15f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
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
        Surface(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.15f),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.25f)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color.copy(alpha = 0.25f),
                            CircleShape
                        )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = label,
            fontSize = 11.sp, // Reduced font size for better fit
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

/* ---------------- PREVIEW ---------------- */

@Preview(showBackground = true)
@Composable
fun PreviewThreatActionRow() {
    val mockThreat = Threat(
        level = ThreatLevel.DANGEROUS,
        score = 95,
        reasons = listOf("Suspicious payment request"),
        sourceNumber = "12345",
        sourceApp = "SMS"
    )

    Box(
        modifier = Modifier
            .background(Color(0xFF0D47A1))
            .padding(20.dp)
    ) {
        ThreatActionRow(threat = mockThreat)
    }
}