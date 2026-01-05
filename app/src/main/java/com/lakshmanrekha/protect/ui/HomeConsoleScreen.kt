package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.*

@Composable
fun HomeConsoleScreen() {
    val context = LocalContext.current

    // Read data directly to ensure the UI recomposes when these change
    val threats = ThreatLogger.getThreats()
    val systemLogs = ThreatLogger.getSystemLogs()
    val currentMode = AppState.protectionMode

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // 👋 Greeting
            Text(
                text = Strings.greeting(AppState.name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )

            Spacer(Modifier.height(20.dp))

            // 🛡️ Reactive Status Card
            StatusCard(currentMode)

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (LanguageManager.isHindi()) "वर्तमान सुरक्षा" else "Current Protection",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(8.dp))

            // 🔁 Mode selector
            ProtectionModeSelector(context)

            Spacer(Modifier.height(32.dp))

            Text(
                text = Strings.recentActivity(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            // 🚨 Reactive Threat List
            Box(modifier = Modifier.weight(1f)) {
                if (threats.isEmpty()) {
                    EmptyThreatState()
                } else {
                    ThreatList(threats = threats)
                }
            }

            // ⚙️ System Logs Footer
            SystemLogsFooter(systemLogs)
        }
    }
}

@Composable
private fun StatusCard(mode: ProtectionMode) {
    val statusColor = protectionColor()

    // Smoothly animate the card background color when protection mode changes
    val animatedBgColor by animateColorAsState(
        targetValue = statusColor.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 500),
        label = "BgAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Shield, contentDescription = null, tint = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = Strings.protectionStatus(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = "System: ${mode.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ThreatItem(threat: Threat) {
    val color = when (threat.level) {
        ThreatLevel.SAFE -> Color(0xFF2E7D32)
        ThreatLevel.CAUTION -> Color(0xFFF9A825)
        ThreatLevel.RISKY -> Color(0xFFE65100)
        ThreatLevel.DANGEROUS -> Color(0xFFB71C1C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = threat.level.name,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 18.sp
                )
                threat.reasons.forEach { reason ->
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyThreatState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = Strings.noThreats(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun SystemLogsFooter(logs: List<String>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        // Correct Material 3 Divider implementation
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        Text("System Logs", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        logs.takeLast(1).forEach { log ->
            Text(
                text = "> $log",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

private fun protectionColor(): Color =
    when (AppState.protectionMode) {
        ProtectionMode.RAKSHA -> Color(0xFF1A73E8)
        ProtectionMode.LAKSHMAN -> Color(0xFF34A853)
        ProtectionMode.SAATHI -> Color(0xFFFBBC04)
        else -> Color(0xFFD93025)
    }

@Composable
private fun ThreatList(threats: List<Threat>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(threats) { threat -> ThreatItem(threat) }
    }
}