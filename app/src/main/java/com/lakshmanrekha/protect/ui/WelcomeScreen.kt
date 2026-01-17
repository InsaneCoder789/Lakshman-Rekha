package com.lakshmanrekha.protect.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    val isHindi = LanguageManager.isHindi()

    // --- THEME COLORS (Midnight Navy & Lakshman Blue) ---
    val lakshmanBlue = Color(0xFF0D47A1)
    val lakshmanDarkNavy = Color(0xFF0A192F)
    val lakshmanAccent = Color(0xFF4FC3F7)

    // Deep gradient background for consistent "Secure" feel
    val bgGradient = Brush.verticalGradient(
        colors = listOf(lakshmanDarkNavy, lakshmanBlue)
    )

    // Pulsing aura animation for the shield icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = lakshmanDarkNavy) {
        Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.4f))

                // --- ICON BRANDING (AURA EFFECT) ---
                Box(contentAlignment = Alignment.Center) {
                    // Outer pulsing rings
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(scale)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale * 0.9f)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    )

                    // Main Shield Icon
                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 15.dp
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GppGood,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp).fillMaxSize(),
                            tint = lakshmanBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // --- GREETING ---
                Text(
                    text = if (isHindi) "नमस्ते, सुरक्षित रहें" else "Namaste, Stay Safe",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 34.sp,
                        letterSpacing = (-1).sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isHindi)
                        "धोखाधड़ी और स्कैम कॉल्स से आपकी सुरक्षा अब हमारी जिम्मेदारी है।"
                    else "Protecting you from fraud and scam calls is now our priority.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(52.dp))

                // --- KEY BENEFITS (UPGRADED GLASS CARDS) ---
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    BenefitCard(
                        text = if (isHindi) "स्कैम कॉल को पहचानें" else "Identify Scam Calls",
                        accentColor = lakshmanAccent
                    )
                    BenefitCard(
                        text = if (isHindi) "धोखाधड़ी वाले संदेश रोकें" else "Block Fraud Messages",
                        accentColor = lakshmanAccent
                    )
                    BenefitCard(
                        text = if (isHindi) "परिवार को सूचित रखें" else "Keep Family Informed",
                        accentColor = lakshmanAccent
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- ACTION BUTTON (SENIOR OPTIMIZED) ---
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = lakshmanBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                ) {
                    Text(
                        text = if (isHindi) "शुरू करें" else "Get Started",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BenefitCard(text: String, accentColor: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            )
        }
    }
}