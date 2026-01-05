package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun ModeExplanationScreen(onContinue: () -> Unit) {
    var step by rememberSaveable { mutableStateOf(0) }
    val isHindi = LanguageManager.isHindi()

    val modeData = listOf(
        ModeInfo(
            title = if (isHindi) "रक्षा मोड (Raksha Mode)" else "Raksha Mode",
            desc = if (isHindi) "कठोर सुरक्षा और तेज़ चेतावनी।" else "Strong protection with loud warnings.",
            icon = "🛑",
            color = Color(0xFFE53935) // Modern Red
        ),
        ModeInfo(
            title = if (isHindi) "लक्ष्ण मोड (Lakshman Mode)" else "Lakshman Mode",
            desc = if (isHindi) "स्पष्ट स्पष्टीकरण के साथ संतुलित सुरक्षा।" else "Balanced protection with clear explanations.",
            icon = "🟡",
            color = Color(0xFFFFB300) // Modern Amber
        ),
        ModeInfo(
            title = if (isHindi) "साथी मोड (Saathi Mode)" else "Saathi Mode",
            desc = if (isHindi) "केवल मार्गदर्शन, कोई रुकावट नहीं।" else "Guidance-only, no interruptions.",
            icon = "🤝",
            color = Color(0xFF1E88E5) // Modern Blue
        )
    )

    val currentMode = modeData[step]

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Sleek Progress Bar
            LinearProgressIndicator(
                progress = { (step + 1).toFloat() / modeData.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .height(8.dp),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                color = currentMode.color,
                trackColor = currentMode.color.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.weight(0.2f))

            // 2. Animated Content Transition
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it } togetherWith fadeOut() + slideOutHorizontally { -it }
                },
                label = "ModeTransition"
            ) { mode ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = mode.color.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, mode.color.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(mode.icon, fontSize = 64.sp)

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = mode.color
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = mode.desc,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. Large Navigation Button
            Button(
                onClick = {
                    if (step < modeData.lastIndex) step++
                    else onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = currentMode.color)
            ) {
                Text(
                    text = if (step < modeData.lastIndex)
                        (if (isHindi) "अगला (Next)" else "Next")
                    else
                        (if (isHindi) "ठीक है, समझ गया" else "Continue"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

data class ModeInfo(val title: String, val desc: String, val icon: String, val color: Color)