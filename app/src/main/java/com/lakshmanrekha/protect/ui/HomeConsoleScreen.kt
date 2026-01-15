package com.lakshmanrekha.protect.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.*

@Composable
fun HomeConsoleScreen() {
    val context = LocalContext.current
    val threats = ThreatLogger.getThreats()
    val systemLogs = ThreatLogger.getSystemLogs()
    val isHindi = LanguageManager.isHindi()

    // UI State for dynamic coloring
    val currentMode = AppState.protectionMode
    val activeBrandColor by animateColorAsState(
        targetValue = getThemeColorForMode(currentMode),
        animationSpec = tween(500), label = "color"
    )

    // Shield Rotation
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing)), label = "rotate"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = activeBrandColor) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- BACKGROUND SHIELD WATERMARK ---
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. GREETING
                item {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = Strings.greeting(AppState.name),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 34.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isHindi) "आज आप पूरी तरह सुरक्षित हैं" else "Your perimeter is secure today",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }

                // 2. STATUS CARD
                item { StatusCard(currentMode, activeBrandColor) }

                // 3. COACH ENTRY (Surface adjusts to brand)
                item { CoachEntryCard(context, isHindi, activeBrandColor) }

                // 4. MODE SELECTOR
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (isHindi) "सुरक्षा मोड चुनें" else "Protection Level",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        ProtectionModeSelector(context)
                    }
                }

                // 5. ACTIVITY HEADER
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Strings.recentActivity(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        if (threats.isNotEmpty()) {
                            Text(
                                text = "${threats.size} Logs",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // 6. THREAT LIST
                if (threats.isEmpty()) {
                    item { EmptyThreatState() }
                } else {
                    items(threats) { threat ->
                        ThreatItem(threat, activeBrandColor)
                    }
                }

                // 7. FOOTER LOGS
                item {
                    SystemLogsFooter(systemLogs)
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusCard(mode: ProtectionMode, brandColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 44f, targetValue = 60f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "pulse"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                Box(Modifier.size(pulseSize.dp).background(Color.White.copy(alpha = 0.2f), CircleShape))
                Surface(Modifier.size(48.dp), shape = CircleShape, color = Color.White) {
                    Icon(Icons.Rounded.GppGood, null, tint = brandColor, modifier = Modifier.padding(10.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    text = Strings.protectionStatus(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black, color = Color.White
                    )
                )
                Text(
                    text = "Mode: ${mode.name}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CoachEntryCard(context: android.content.Context, isHindi: Boolean, brandColor: Color) {
    Card(
        onClick = { CoachLauncher.launch(context) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(Modifier.size(44.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                Icon(Icons.Rounded.AutoStories, null, tint = Color.White, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (isHindi) "सुरक्षा मार्गदर्शिका" else "Safety Guide",
                    fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White
                )
                Text(
                    text = if (isHindi) "धोखाधड़ी से कैसे बचें" else "Master scam detection",
                    fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(Icons.Rounded.ArrowForwardIos, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ThreatItem(threat: Threat, brandColor: Color) {
    val color = when (threat.level) {
        ThreatLevel.SAFE -> Color(0xFF66BB6A)
        ThreatLevel.CAUTION -> Color(0xFFFFA726)
        ThreatLevel.RISKY -> Color(0xFFFF7043)
        ThreatLevel.DANGEROUS -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    Modifier.size(42.dp),
                    shape = CircleShape,
                    color = color.copy(0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.4f))
                ) {
                    Icon(Icons.Rounded.Radar, null, tint = color, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = threat.level.name,
                        fontWeight = FontWeight.Black,
                        color = color,
                        fontSize = 18.sp
                    )
                    threat.reasons.forEach {
                        Text(it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            if (threat.level != ThreatLevel.SAFE) {
                Spacer(Modifier.height(16.dp))
                ThreatActionRow(threat)
            }
        }
    }
}

@Composable
private fun EmptyThreatState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.Verified, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text(text = Strings.noThreats(), color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SystemLogsFooter(logs: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.3f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Terminal, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = logs.lastOrNull() ?: "System Standby",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Global Theme Color Resolver
private fun getThemeColorForMode(mode: ProtectionMode): Color = when (mode) {
    ProtectionMode.SAATHI -> Color(0xFF0D47A1)   // Blue
    ProtectionMode.RAKSHA -> Color(0xFFC5A000)   // Golden
    ProtectionMode.LAKSHMAN -> Color(0xFFE65100) // Orange
    else -> Color(0xFF0D47A1)
}

@Preview
@Composable
fun PreviewHome() {
    MaterialTheme { HomeConsoleScreen() }
}