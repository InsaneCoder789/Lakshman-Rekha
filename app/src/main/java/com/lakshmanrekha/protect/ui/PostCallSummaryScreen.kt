package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.RuntimeState

@Composable
fun PostCallSummaryScreen(onDismiss: () -> Unit) {

    val isHindi = LanguageManager.isHindi()
    val level = RuntimeState.lastThreatLevel ?: ThreatLevel.SAFE
    val reasons = RuntimeState.lastThreatReasons

    val (color, title) = when (level) {
        ThreatLevel.SAFE ->
            Color(0xFF2E7D32) to
                    if (isHindi) "कोई खतरा नहीं" else "No Risk Detected"

        ThreatLevel.CAUTION ->
            Color(0xFFF9A825) to
                    if (isHindi) "सावधानी" else "Caution"

        ThreatLevel.RISKY ->
            Color(0xFFEF6C00) to
                    if (isHindi) "जोखिम भरा" else "Risky Situation"

        ThreatLevel.DANGEROUS ->
            Color(0xFFC62828) to
                    if (isHindi) "गंभीर खतरा" else "High Scam Risk"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Text(
                text = if (isHindi) "कॉल के बाद सुरक्षा रिपोर्ट" else "Post-Call Safety Summary",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        color = color,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text =
                            if (isHindi)
                                "यह कॉल सुरक्षित नहीं हो सकती थी।"
                            else
                                "This call showed signs of a possible scam.",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (isHindi) "कारण:" else "Why this was flagged:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            reasons.forEach {
                Text(
                    text = "• $it",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isHindi) "समझ गया / गई" else "I Understand",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}