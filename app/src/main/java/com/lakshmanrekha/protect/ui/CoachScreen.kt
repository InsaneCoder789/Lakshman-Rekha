package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun CoachScreen(
    onDone: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ---------- HEADER ----------
        Text(
            text = "🛡️ How to Stay Safe from Scams\nठगी से कैसे सुरक्षित रहें",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text =
                "This guide helps you understand common scam tricks.\n" +
                        "यह गाइड आपको आम ठगी के तरीकों को समझने में मदद करती है।",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // ---------- SECTION 1 ----------
        CoachCard(
            title = "🔐 OTP & Bank Scams",
            english =
                "Banks never ask for OTP, PIN, or card details on calls or messages.",
            hindi =
                "बैंक कभी भी कॉल या मैसेज पर OTP, PIN या कार्ड की जानकारी नहीं मांगते।"
        )

        // ---------- SECTION 2 ----------
        CoachCard(
            title = "📱 UPI / QR Code Fraud",
            english =
                "Receiving money does NOT require scanning any QR code.",
            hindi =
                "पैसे प्राप्त करने के लिए QR कोड स्कैन करने की जरूरत नहीं होती।"
        )

        // ---------- SECTION 3 ----------
        CoachCard(
            title = "⏰ Urgency & Fear Tactics",
            english =
                "Scammers create fear or urgency so you act without thinking.",
            hindi =
                "ठग डर या जल्दबाज़ी पैदा करते हैं ताकि आप बिना सोचे फैसला लें।"
        )

        // ---------- SECTION 4 ----------
        CoachCard(
            title = "🚓 Fake Government / Police Calls",
            english =
                "Government or police never threaten arrest over phone calls.",
            hindi =
                "सरकार या पुलिस फोन पर गिरफ़्तारी की धमकी नहीं देती।"
        )

        // ---------- SECTION 5 ----------
        CoachCard(
            title = "✅ Safe Habits",
            english =
                "• Do not share OTP\n" +
                        "• Do not click unknown links\n" +
                        "• Verify with family\n" +
                        "• Take time to decide",
            hindi =
                "• OTP साझा न करें\n" +
                        "• अनजान लिंक पर क्लिक न करें\n" +
                        "• परिवार से पुष्टि करें\n" +
                        "• फैसला लेने में समय लें"
        )

        Spacer(Modifier.height(10.dp))

        // ---------- DONE BUTTON ----------
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "I Understand / मैं समझ गया",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------------------------------------------
 * REUSABLE CARD
 * --------------------------------------------------- */

@Composable
private fun CoachCard(
    title: String,
    english: String,
    hindi: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "English:\n$english",
                fontSize = 16.sp
            )

            Text(
                text = "हिंदी:\n$hindi",
                fontSize = 16.sp
            )
        }
    }
}