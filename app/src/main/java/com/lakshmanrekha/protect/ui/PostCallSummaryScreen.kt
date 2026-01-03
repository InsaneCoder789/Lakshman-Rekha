package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.RuntimeState

@Composable
fun PostCallSummaryScreen(onDone: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Call Summary",
            fontSize = 22.sp
        )

        Spacer(Modifier.height(16.dp))

        RuntimeState.lastCallReasons.forEach {
            Text("• $it", fontSize = 16.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDone
        ) {
            Text("OK")
        }
    }
}