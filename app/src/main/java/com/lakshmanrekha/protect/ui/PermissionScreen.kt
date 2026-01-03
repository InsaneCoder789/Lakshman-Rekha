package com.lakshmanrekha.protect.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionScreen(onContinue: () -> Unit) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Permissions Needed",
            fontSize = 26.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = """
We need a few permissions to protect you:

• Read messages to detect scams
• Show warning on screen
• Help during suspicious calls

We do NOT read personal chats.
            """.trimIndent(),
            fontSize = 18.sp
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                context.startActivity(
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Allow Notification Access")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                context.startActivity(
                    Intent("android.settings.ACTION_ACCESSIBILITY_SETTINGS")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Allow Accessibility Access")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}