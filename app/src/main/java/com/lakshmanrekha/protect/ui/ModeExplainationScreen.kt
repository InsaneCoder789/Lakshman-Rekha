package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager

@Composable
fun ModeExplanationScreen(onContinue: () -> Unit) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    val isHindi = LanguageManager.isHindi()

    // Colors synced with ProtectionModeSelector
    val saathiBlue = Color(0xFF0D47A1)
    val rakshaGold = Color(0xFFEFBF04)
    val lakshmanRed = Color(0xFFBF360C)

    val modeData = listOf(
        ModeInfo(
            title = if (isHindi) "साथी मोड (Saathi)" else "Saathi Mode",
            desc = if (isHindi)
                "यह केवल मार्गदर्शन देता है। कोई कॉल या ऐप ब्लॉक नहीं करता।"
            else
                "Advisory-only mode. No blocking or intervention.",
            bestFor = if (isHindi) "स्वतंत्र उपयोगकर्ताओं के लिए" else "Best for: Independent users",
            icon = Icons.Rounded.Handshake,
            color = saathiBlue
        ),
        ModeInfo(
            title = if (isHindi) "रक्षा मोड (Raksha)" else "Raksha Mode",
            desc = if (isHindi)
                "यह चेतावनी देता है और सही कदम सुझाता है।"
            else
                "Guided protection with warnings and coaching.",
            bestFor = if (isHindi) "दैनिक सुरक्षा के लिए" else "Best for: Daily protection",
            icon = Icons.Rounded.GppGood,
            color = rakshaGold
        ),
        ModeInfo(
            title = if (isHindi) "लक्ष्मण मोड (Lakshman)" else "Lakshman Mode",
            desc = if (isHindi)
                "यह सबसे मजबूत सुरक्षा है। सिस्टम स्वयं कार्रवाई करता है।"
            else
                "Maximum protection. The system acts autonomously.",
            bestFor = if (isHindi) "उच्च जोखिम वाले उपयोगकर्ताओं के लिए" else "Best for: High-risk users",
            icon = Icons.Rounded.Shield,
            color = lakshmanRed
        )
    )

    val currentMode = modeData[step]
    val lakshmanDarkNavy = Color(0xFF0A192F)

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(lakshmanDarkNavy, currentMode.color.copy(alpha = 0.15f)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Surface(modifier = Modifier.fillMaxSize(), color = lakshmanDarkNavy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. DYNAMIC STEP INDICATOR
            Row(
                Modifier.padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(modeData.size) { index ->
                    val isSelected = index == step
                    val width = if (isSelected) 48.dp else 12.dp
                    val color = if (isSelected) currentMode.color else Color.White.copy(alpha = 0.2f)

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .background(color, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // 2. ANIMATED CONTENT HUB
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.85f)).togetherWith(fadeOut() + scaleOut(targetScale = 1.15f))
                },
                label = "ModeTransition"
            ) { mode ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(160.dp),
                        shape = CircleShape,
                        color = mode.color.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, mode.color.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = null,
                            modifier = Modifier.padding(40.dp).fillMaxSize(),
                            tint = mode.color
                        )
                    }

                    Spacer(Modifier.height(48.dp))

                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 36.sp,
                            letterSpacing = (-1).sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Surface(
                        color = mode.color,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = mode.bestFor,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                // Dark text for the Gold color, white for others
                                color = if (mode.color == rakshaGold) Color.Black else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = mode.desc,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 22.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. NAVIGATION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .weight(0.35f)
                            .height(64.dp), // Slightly shorter than main button
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isHindi) "पीछे" else "Back",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 12.sp, // Reduced size
                                fontWeight = FontWeight.Medium // Reduced weight
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < modeData.lastIndex) step++
                        else onContinue()
                    },
                    modifier = Modifier.weight(1f).height(72.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (step == modeData.lastIndex) currentMode.color else Color.White,
                        contentColor = if (step == modeData.lastIndex) {
                            if (currentMode.color == rakshaGold) Color.Black else Color.White
                        } else lakshmanDarkNavy
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                ) {
                    Text(
                        text = if (step < modeData.lastIndex)
                            (if (isHindi) "अगला" else "Next")
                        else
                            (if (isHindi) "तैयार हूँ" else "I'm Ready"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class ModeInfo(
    val title: String,
    val desc: String,
    val bestFor: String,
    val icon: ImageVector,
    val color: Color
)