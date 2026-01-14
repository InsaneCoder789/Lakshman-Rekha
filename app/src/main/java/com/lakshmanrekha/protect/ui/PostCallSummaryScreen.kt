package com.lakshmanrekha.protect.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.RuntimeState

@Composable
fun PostCallSummaryScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isHindi = LanguageManager.isHindi()

    val bgBlue = Color(0xFF0D47A1)
    val cardSurface = Color(0xFF1565C0)

    val level = RuntimeState.lastThreatLevel ?: ThreatLevel.SAFE
    val reasons = RuntimeState.lastThreatReasons

    val threat = Threat(
        level = level, score = 0, reasons = reasons,
        sourceNumber = RuntimeState.activeSourceNumber,
        sourceApp = RuntimeState.activeSourceApp
    )

    val (statusColor, title, icon) = when (level) {
        ThreatLevel.SAFE -> Triple(Color(0xFF66BB6A), if (isHindi) "सुरक्षित" else "Safe Call", Icons.Rounded.Verified)
        ThreatLevel.CAUTION -> Triple(Color(0xFFFFA726), if (isHindi) "सावधानी" else "Caution", Icons.Rounded.Warning)
        ThreatLevel.RISKY -> Triple(Color(0xFFFF7043), if (isHindi) "जोखिम" else "Risky", Icons.Rounded.GppMaybe)
        ThreatLevel.DANGEROUS -> Triple(Color(0xFFEF5350), if (isHindi) "खतरा" else "Danger", Icons.Rounded.Dangerous)
    }

    // --- NEW: Background Rotating Shield Animation ---
    val infiniteRotation = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteRotation.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "rotation"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgBlue) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- NEW: Sublte Background Watermark ---
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.03f),
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))

                // ---------- PULSING HEADER ----------
                Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                    // Pulsing Rings
                    val pulseScale by rememberInfiniteTransition().animateFloat(
                        initialValue = 1f, targetValue = 1.4f,
                        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse)
                    )
                    Box(Modifier.size(80.dp).alpha(0.15f * (2f - pulseScale)).background(statusColor, CircleShape).padding(pulseScale.dp))

                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = CircleShape,
                        color = statusColor,
                        shadowElevation = 12.dp
                    ) {
                        Icon(icon, null, tint = Color.White, modifier = Modifier.padding(24.dp))
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black, color = Color.White
                    )
                )

                // --- NEW: Dynamic Status Line ---
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = if (isHindi) "लाक्ष्मण रेखा सुरक्षा स्कैन" else "Lakshman Rekha Secured",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                // ---------- GLASS-MORPHISM DATA CARD ----------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Analytics, null, tint = Color.White.copy(alpha = 0.6f))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "जांच का परिणाम" else "Security Analysis",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        if (reasons.isEmpty()) {
                            Text(
                                if (isHindi) "यह कॉल सुरक्षित पाई गई।" else "Security scan complete. No threats found.",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        } else {
                            reasons.forEach { reason ->
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Rounded.Radar, null, tint = statusColor, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Text(reason, color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp, lineHeight = 20.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ---------- ACTIONS ----------
                if (level != ThreatLevel.SAFE) {
                    OneTapActions(
                        context = context,
                        threat = threat,
                        onDismiss = {
                            RuntimeState.resetSession()
                            onDismiss()
                        }
                    )
                }

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(32.dp))

                // ---------- DONE BUTTON ----------
                Button(
                    onClick = {
                        RuntimeState.resetSession()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = bgBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = if (isHindi) "ठीक है" else "Understood",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(name = "Risky State")
@Composable
fun PreviewPostCallRisky() {
    RuntimeState.lastThreatLevel = ThreatLevel.RISKY
    RuntimeState.lastThreatReasons = listOf("Detected request for PIN/OTP", "High pressure speech pattern")
    MaterialTheme { PostCallSummaryScreen {} }
}