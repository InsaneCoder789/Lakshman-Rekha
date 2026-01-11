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
import androidx.compose.material.icons.rounded.MenuBook
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
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.*

@Composable
fun HomeConsoleScreen() {
    val context = LocalContext.current

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

            // 🛡️ Status
            StatusCard(currentMode)

            // 🧠 COACH ENTRY (NEW)
            Spacer(Modifier.height(16.dp))
            CoachEntryCard(context)

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

            // 🚨 Threat List
            Box(modifier = Modifier.weight(1f)) {
                if (threats.isEmpty()) {
                    EmptyThreatState()
                } else {
                    ThreatList(threats = threats)
                }
            }

            // ⚙️ Logs
            SystemLogsFooter(systemLogs)
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               COACH ENTRY                                  */
/* -------------------------------------------------------------------------- */

@Composable
private fun CoachEntryCard(context: android.content.Context) {
    Card(
        onClick = { CoachLauncher.launch(context) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = if (LanguageManager.isHindi())
                        "सुरक्षा मार्गदर्शिका"
                    else
                        "Safety Guide",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Text(
                    text = if (LanguageManager.isHindi())
                        "ठगी से बचने के तरीके सीखें"
                    else
                        "Learn how scams work & how to stay safe",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               EXISTING UI                                  */
/* -------------------------------------------------------------------------- */

@Composable
private fun StatusCard(mode: ProtectionMode) {
    val statusColor = protectionColor()

    val animatedBgColor by animateColorAsState(
        targetValue = statusColor.copy(alpha = 0.1f),
        animationSpec = tween(500),
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
                Icon(Icons.Rounded.Shield, null, tint = Color.White)
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        color = color
                    )
                    threat.reasons.forEach {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // ✅ ONE-TAP ACTIONS HERE
            if (threat.level != ThreatLevel.SAFE) {
                ThreatActionRow(threat)
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
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun SystemLogsFooter(logs: List<String>) {
    Column(Modifier.padding(vertical = 16.dp)) {
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Text("System Logs", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        logs.takeLast(1).forEach {
            Text(
                text = "> $it",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.6f)
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
private fun ThreatList(threats: List<Threat>) {
    LazyColumn {
        items(threats) { ThreatItem(it) }
    }
}