package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.Strings

@Composable
fun HomeConsoleScreen() {

    val threats = com.lakshmanrekha.protect.utils.ThreatLogger.getThreats()
    val systemLogs = com.lakshmanrekha.protect.utils.ThreatLogger.getSystemLogs()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // 👋 Greeting (Bilingual)
        Text(
            text = Strings.greeting(AppState.name),
            fontSize = 26.sp
        )

        Spacer(Modifier.height(8.dp))

        // 🛡️ Protection Status (Bilingual)
        Text(
            text = Strings.protectionStatus(),
            fontSize = 18.sp,
            color = protectionColor()
        )

        // 🔧 Downgrade explanation (if any)
        AppState.lastDowngradeReason?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(20.dp))

        // 🚨 Recent Threats
        Text(
            text = Strings.recentActivity(),
            fontSize = 20.sp
        )

        Spacer(Modifier.height(8.dp))

        if (threats.isEmpty()) {
            Text(
                text = Strings.noThreats(),
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            ThreatList(threats)
        }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        // ⚙️ System Logs
        Text(
            text = Strings.systemStatus(),
            fontSize = 18.sp
        )

        Spacer(Modifier.height(6.dp))

        systemLogs.forEach {
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ThreatList(threats: List<Threat>) {
    LazyColumn {
        items(threats) { threat ->
            ThreatItem(threat)
        }
    }
}

@Composable
private fun ThreatItem(threat: Threat) {

    val color = when (threat.level) {
        ThreatLevel.SAFE -> Color(0xFF2E7D32)
        ThreatLevel.CAUTION -> Color(0xFFF9A825)
        ThreatLevel.RISKY -> Color(0xFFEF6C00)
        ThreatLevel.DANGEROUS -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = threat.level.name,
                fontSize = 16.sp,
                color = color
            )

            Spacer(Modifier.height(4.dp))

            threat.reasons.forEach {
                Text(
                    text = "• $it",
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun protectionColor(): Color =
    when (AppState.protectionMode) {
        com.lakshmanrekha.protect.utils.ProtectionMode.RAKSHA -> Color(0xFF2E7D32)
        com.lakshmanrekha.protect.utils.ProtectionMode.LAKSHMAN -> Color(0xFFF9A825)
        com.lakshmanrekha.protect.utils.ProtectionMode.SAATHI -> Color(0xFF388E3C)
        com.lakshmanrekha.protect.utils.ProtectionMode.NONE -> Color(0xFFC62828)
    }