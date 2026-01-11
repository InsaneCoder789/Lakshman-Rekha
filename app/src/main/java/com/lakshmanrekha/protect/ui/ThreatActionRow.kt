package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lakshmanrekha.protect.core.ThreatActionHandler
import com.lakshmanrekha.protect.model.Threat

@Composable
fun ThreatActionRow(
    threat: Threat,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ActionButton(
            label = "Call",
            icon = Icons.Rounded.Call,
            color = Color(0xFF1E88E5)
        ) {
            ThreatActionHandler.callTrustedContact(context)
        }

        ActionButton(
            label = "Block",
            icon = Icons.Rounded.Block,
            color = Color(0xFFE53935)
        ) {
            ThreatActionHandler.blockSource(context, threat)
        }

        ActionButton(
            label = "Report",
            icon = Icons.Rounded.Report,
            color = Color(0xFFF9A825)
        ) {
            ThreatActionHandler.reportScam(threat)
        }

        ActionButton(
            label = "Verify",
            icon = Icons.Rounded.Verified,
            color = Color(0xFF43A047)
        ) {
            ThreatActionHandler.verifyMerchant(context)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(6.dp))
        Text(label)
    }
}