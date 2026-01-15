package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.GppMaybe
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
    var step by rememberSaveable { mutableStateOf(0) }
    val isHindi = LanguageManager.isHindi()

    val modeData = listOf(

        ModeInfo(
            title = if (isHindi) "साथी मोड (Saathi)" else "Saathi Mode",
            desc = if (isHindi)
                "यह केवल मार्गदर्शन देता है। कोई कॉल या ऐप ब्लॉक नहीं करता।"
            else
                "Advisory-only mode. No blocking or intervention.",
            bestFor = if (isHindi) "स्वतंत्र उपयोगकर्ताओं के लिए" else "Best for: Independent users",
            icon = Icons.Rounded.GppGood,
            color = Color(0xFF1976D2)
        ),

        ModeInfo(
            title = if (isHindi) "रक्षा मोड (Raksha)" else "Raksha Mode",
            desc = if (isHindi)
                "यह चेतावनी देता है और सही कदम सुझाता है।"
            else
                "Guided protection with warnings and coaching.",
            bestFor = if (isHindi) "दैनिक सुरक्षा के लिए" else "Best for: Daily protection",
            icon = Icons.Rounded.GppMaybe,
            color = Color(0xFFF57C00)
        ),

        ModeInfo(
            title = if (isHindi) "लक्ष्मण मोड (Lakshman)" else "Lakshman Mode",
            desc = if (isHindi)
                "यह सबसे मजबूत सुरक्षा है। सिस्टम स्वयं कार्रवाई करता है।"
            else
                "Maximum protection. The system acts autonomously.",
            bestFor = if (isHindi) "उच्च जोखिम वाले उपयोगकर्ताओं के लिए" else "Best for: High-risk users",
            icon = Icons.Rounded.Shield,
            color = Color(0xFFD32F2F)
        )
    )

    val currentMode = modeData[step]

    // Background Gradient based on current mode color
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(currentMode.color.copy(alpha = 0.15f), Color.Transparent),
        startY = 0f,
        endY = 1000f
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Modern Page Indicator (Dots)
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(modeData.size) { index ->
                    val width = if (index == step) 32.dp else 8.dp
                    val color = if (index == step) currentMode.color else currentMode.color.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .background(color, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 2. Animated Content
            AnimatedContent(
                targetState = currentMode,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { it }).togetherWith(fadeOut() + slideOutHorizontally { -it })
                },
                label = "ModeTransition"
            ) { mode ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Icon with Glow
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = mode.color.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp).fillMaxSize(),
                            tint = mode.color
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = mode.color,
                            letterSpacing = (-1).sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Contextual "Best For" Badge
                    Surface(
                        color = mode.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = mode.bestFor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = mode.color,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = mode.desc,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Back Button (Only if not on first step)
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(0.4f).height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, currentMode.color.copy(alpha = 0.5f))
                    ) {
                        Text(if (isHindi) "पीछे" else "Back", color = currentMode.color)
                    }
                }

                Button(
                    onClick = {
                        if (step < modeData.lastIndex) step++
                        else onContinue()
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = currentMode.color),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = if (step < modeData.lastIndex)
                            (if (isHindi) "अगला" else "Next")
                        else
                            (if (isHindi) "तैयार हूँ" else "I'm Ready"),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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