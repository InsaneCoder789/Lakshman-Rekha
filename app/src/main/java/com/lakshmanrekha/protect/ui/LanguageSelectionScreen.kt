package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.AppLanguage

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var selectedLang by remember { mutableStateOf<AppLanguage?>(null) }
    val scrollState = rememberScrollState()

    /* ---------------- BRAND COLORS (UPGRADED PALETTE) ---------------- */
    val sapphireDark = Color(0xFF060B15)
    val sapphirePrimary = Color(0xFF0D5ED4)
    val sapphireAccent = Color(0xFF4FC3F7)
    val glassWhite = Color.White.copy(alpha = 0.05f)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = sapphireDark
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(sapphirePrimary.copy(alpha = 0.25f), sapphireDark)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(72.dp))

                /* ---------------- ICON HUB ---------------- */
                Box(contentAlignment = Alignment.Center) {
                    // Outer Radial Glow
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale)
                            .background(
                                Brush.radialGradient(listOf(sapphireAccent.copy(alpha = 0.12f), Color.Transparent)),
                                CircleShape
                            )
                    )

                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.15f)),
                        shadowElevation = 20.dp
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Language,
                            contentDescription = null,
                            modifier = Modifier.padding(30.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(44.dp))

                /* ---------------- HEADERS ---------------- */
                Text(
                    text = "LAKSHMAN REKHA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = sapphireAccent,
                        letterSpacing = 4.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose Language",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "अपनी भाषा चुनें",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(52.dp))

                /* ---------------- LANGUAGE OPTIONS ---------------- */

                LanguageTile(
                    title = "English",
                    subtitle = "Safe Digital Companion",
                    isSelected = selectedLang == AppLanguage.ENGLISH,
                    accentColor = sapphirePrimary,
                    onClick = { selectedLang = AppLanguage.ENGLISH }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LanguageTile(
                    title = "हिंदी",
                    subtitle = "आपका डिजिटल सुरक्षा साथी",
                    isSelected = selectedLang == AppLanguage.HINDI,
                    accentColor = sapphirePrimary,
                    onClick = { selectedLang = AppLanguage.HINDI }
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                /* ---------------- CONTINUE BUTTON ---------------- */
                AnimatedVisibility(
                    visible = selectedLang != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Button(
                        onClick = { selectedLang?.let { onLanguageSelected(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .shadow(
                                elevation = 25.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = sapphirePrimary,
                                spotColor = sapphirePrimary
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = sapphireDark
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = if (selectedLang == AppLanguage.HINDI) "आगे बढ़ें" else "Continue",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Rounded.ArrowForward, null, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun LanguageTile(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (isSelected) accentColor else Color.White.copy(alpha = 0.12f),
        animationSpec = tween(400),
        label = "border"
    )

    val containerColor by animateColorAsState(
        if (isSelected) accentColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f),
        animationSpec = tween(400),
        label = "container"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        shape = RoundedCornerShape(28.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 0.2.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                    .border(1.5.dp, if (isSelected) Color.White.copy(alpha = 0.5f) else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}