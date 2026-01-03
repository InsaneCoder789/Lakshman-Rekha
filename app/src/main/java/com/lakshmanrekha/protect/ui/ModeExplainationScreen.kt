package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ProtectionMode

@Composable
fun ModeExplanationScreen(onContinue: () -> Unit) {

    var step by remember { mutableStateOf(0) }

    val modes = listOf(
        Triple(
            "🛑 Raksha Mode",
            "Strong protection.\nWe will warn loudly if someone asks for OTP or money.",
            ProtectionMode.RAKSHA
        ),
        Triple(
            "🟡 Lakshman Mode",
            "Balanced protection.\nWe will warn you calmly and explain.",
            ProtectionMode.LAKSHMAN
        ),
        Triple(
            "🤝 Saathi Mode",
            "Guide-only mode.\nWe will only give advice.",
            ProtectionMode.SAATHI
        )
    )

    val current = modes[step]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(current.first, fontSize = 26.sp)
        Spacer(Modifier.height(20.dp))
        Text(current.second, fontSize = 18.sp)
        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                AppState.protectionMode = current.third
                if (step < modes.lastIndex) {
                    step++
                } else {
                    onContinue()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (step < modes.lastIndex) "Next" else "Continue")
        }
    }
}