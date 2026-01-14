package com.lakshmanrekha.protect.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoachScreen(onDone: () -> Unit) {
    val bgBlue = Color(0xFF0D47A1)
    val cardSurface = Color(0xFF1565C0)
    val brandGreen = Color(0xFF66BB6A)

    // Background Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing)), label = "rotate"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgBlue) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- BACKGROUND WATERMARK ---
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.04f),
                modifier = Modifier
                    .size(450.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ---------- CENTERED HEADER ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Rounded.GppGood,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Safety Guide",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )

                    Surface(
                        color = brandGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "सुरक्षा निर्देश",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = brandGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ---------- LESSON CARDS ----------
                    CoachCard(
                        icon = Icons.Rounded.PhonelinkLock,
                        title = "OTP & Bank Details",
                        english = "Banks never ask for OTP or PIN on calls.",
                        hindi = "बैंक कभी भी फोन पर OTP या PIN नहीं मांगते।",
                        cardColor = cardSurface
                    )

                    CoachCard(
                        icon = Icons.Rounded.QrCodeScanner,
                        title = "UPI / QR Codes",
                        english = "Scanning a QR code is only for SENDING money, not receiving.",
                        hindi = "QR कोड सिर्फ पैसे भेजने के लिए होता है, पाने के लिए नहीं।",
                        cardColor = cardSurface
                    )

                    CoachCard(
                        icon = Icons.Rounded.NotificationImportant,
                        title = "Urgency & Threats",
                        english = "Scammers scare you to act fast. Stop and think.",
                        hindi = "ठग आपको डराकर जल्दबाज़ी कराते हैं। रुकें और सोचें।",
                        cardColor = cardSurface
                    )

                    // ---------- SPECIAL CHECKLIST CARD ----------
                    HabitsCard(accentColor = brandGreen)

                    Spacer(Modifier.height(24.dp))

                    // ---------- ACTION BUTTON ----------
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = bgBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("I Understand", fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Text("मैं समझ गया/गयी", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun CoachCard(
    icon: ImageVector,
    title: String,
    english: String,
    hindi: String,
    cardColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = english,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.2f)
                )

                Text(
                    text = hindi,
                    color = Color(0xFF90CAF9), // Light blue accent for Hindi text
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun HabitsCard(accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Stars, null, tint = accentColor)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Safe Habits / सुरक्षित आदतें",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            val habits = listOf(
                "Never share OTP" to "OTP कभी साझा न करें",
                "Don't click unknown links" to "अनजान लिंक पर क्लिक न करें",
                "Ask Family first" to "पहले परिवार से पूछें"
            )

            habits.forEach { (eng, hin) ->
                Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = accentColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(eng, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(hin, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewCoachScreen() {
    MaterialTheme {
        CoachScreen(onDone = {})
    }
}