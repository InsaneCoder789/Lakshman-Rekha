package com.lakshmanrekha.protect.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
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

    val currentMode = AppState.protectionMode
    val activeBrandColor by animateColorAsState(
        targetValue = getThemeColorForMode(currentMode),
        animationSpec = tween(700), label = "backgroundTransition"
    )

    // Sleek Rotation for the background shield
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(40000, easing = LinearEasing)), label = "rotate"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = activeBrandColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Modern Ambient Background Gradient
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                )
            ))

            // Subtle Animated Watermark
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(600.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. PREMIUM GREETING
                item {
                    Column(modifier = Modifier.padding(top = 40.dp)) {
                        Text(
                            text = Strings.greeting(AppState.name),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp,
                                color = Color.White,
                                letterSpacing = (-1).sp
                            )
                        )
                        Text(
                            text = if (isHindi) "आपकी सुरक्षा सक्रिय है" else "Your perimeter is fully active",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 2. HERO STATUS CARD (Modern Glassmorphism)
                item { StatusCard(currentMode, activeBrandColor) }

                // 3. INTERCHANGED: PROTECTION SELECTOR FIRST (Prioritizing Control)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = if (isHindi) "सुरक्षा का स्तर चुनें" else "Set Protection Level",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                        ProtectionModeSelector(context)
                    }
                }

                // 4. INTERCHANGED: SAFETY GUIDE SECOND (Supportive)
                item { CoachEntryCard(context, isHindi) }

                // 5. ACTIVITY SECTION
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Strings.recentActivity(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (threats.isNotEmpty()) "${threats.size} events" else "Secure",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 6. DYNAMIC THREAT LIST
                if (threats.isEmpty()) {
                    item { EmptyThreatState() }
                } else {
                    items(threats) { threat ->
                        ThreatItem(threat)
                    }
                }

                // 7. TERMINAL FOOTER
                item {
                    SystemLogsFooter(systemLogs)
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusCard(mode: ProtectionMode, brandColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "pulse"
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // High-Visibility Status Orb
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Surface(
                    modifier = Modifier.size(70.dp).alpha(pulseAlpha),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.4f)
                ) {}
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Security,
                        null,
                        tint = brandColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.width(20.dp))

            Column {
                Text(
                    text = if (LanguageManager.isHindi()) "सुरक्षा कवच" else "SHIELD ACTIVE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = mode.name,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black, color = Color.White, fontSize = 32.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun CoachEntryCard(context: android.content.Context, isHindi: Boolean) {
    Card(
        onClick = { CoachLauncher.launch(context) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoStories, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (isHindi) "सुरक्षा गाइड" else "Senior Safety Guide",
                    fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White
                )
                Text(
                    text = if (isHindi) "धोखाधड़ी से कैसे बचें" else "Learn to spot phone scams",
                    fontSize = 15.sp, color = Color.White.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ThreatItem(threat: Threat) {
    val color = when (threat.level) {
        ThreatLevel.SAFE -> Color(0xFF81C784)
        ThreatLevel.CAUTION -> Color(0xFFFFB74D)
        ThreatLevel.RISKY -> Color(0xFFFF8A65)
        ThreatLevel.DANGEROUS -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(0.15f),
                border = BorderStroke(1.dp, color.copy(0.3f))
            ) {
                Icon(Icons.Rounded.Radar, null, tint = color, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = threat.level.name,
                    fontWeight = FontWeight.Black,
                    color = color,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                threat.reasons.forEach {
                    Text(it, fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                }
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
        Icon(Icons.Rounded.VerifiedUser, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(70.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            text = Strings.noThreats(),
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun SystemLogsFooter(logs: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Terminal, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = logs.lastOrNull() ?: "All systems standing by...",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

private fun getThemeColorForMode(mode: ProtectionMode): Color = when (mode) {
    ProtectionMode.SAATHI -> Color(0xFF0D47A1)   // Deep Royal Blue
    ProtectionMode.RAKSHA -> Color(0xFF8B7500)   // Rich Dark Gold
    ProtectionMode.LAKSHMAN -> Color(0xFFBF360C) // Deep Burned Orange
    else -> Color(0xFF0D47A1)
}