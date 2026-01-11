package com.lakshmanrekha.protect.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.core.ThreatActionHandler
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun OneTapActions(
    context: Context,
    threat: Threat,
    onDismiss: (() -> Unit)? = null
) {
    val isHindi = LanguageManager.isHindi()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (isHindi) "तुरंत कार्रवाई करें" else "Take Action Now",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // 📞 CALL TRUSTED CONTACT
            ActionButton(
                icon = Icons.Rounded.Person,
                label = if (isHindi) "परिवार को कॉल करें" else "Call Trusted Contact",
                color = Color(0xFF1E88E5)
            ) {
                ThreatActionHandler.callTrustedContact(context)
                onDismiss?.invoke()
            }

            // 🚫 BLOCK SOURCE
            ActionButton(
                icon = Icons.Rounded.Block,
                label = if (isHindi) "ब्लॉक करें" else "Block / Stop",
                color = Color(0xFFF57C00)
            ) {
                ThreatActionHandler.blockSource(context, threat)
                onDismiss?.invoke()
            }

            // 🧠 SAFETY COACH
            ActionButton(
                icon = Icons.Rounded.School,
                label = if (isHindi) "सुरक्षा मार्गदर्शक" else "Safety Coach",
                color = Color(0xFF43A047)
            ) {
                CoachLauncher.launch(context)
                onDismiss?.invoke()
            }

            // 🚨 REPORT SCAM
            ActionButton(
                icon = Icons.Rounded.Report,
                label = if (isHindi) "रिपोर्ट करें" else "Report Scam",
                color = Color(0xFFC62828)
            ) {
                ThreatActionHandler.reportScam(threat)
                onDismiss?.invoke()
            }

            // 🌐 VERIFY MERCHANT
            ActionButton(
                icon = Icons.Rounded.Verified,
                label = if (isHindi) "व्यापारी सत्यापित करें" else "Verify Merchant",
                color = Color(0xFF6A1B9A)
            ) {
                ThreatActionHandler.verifyMerchant(context)
                onDismiss?.invoke()
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}