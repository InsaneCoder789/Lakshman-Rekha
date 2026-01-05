package com.lakshmanrekha.protect.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.utils.*

class SOSActivity : ComponentActivity() {

    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                sendSosMessages()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔴 Send SOS automatically when screen opens (after permission)
        requestSmsPermissionIfNeeded()

        setContent {
            SOSScreen {
                finish()
            }
        }
    }

    private fun requestSmsPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        } else {
            sendSosMessages()
        }
    }

    private fun sendSosMessages() {

        if (AppState.trustedContacts.isEmpty()) {
            ThreatLogger.logSystem("SOS aborted: no trusted contacts")
            return
        }

        val smsManager = SmsManager.getDefault()

        val message =
            if (LanguageManager.isHindi())
                "🚨 आपातकालीन संदेश!\nमुझे तुरंत मदद की आवश्यकता है। कृपया संपर्क करें।"
            else
                "🚨 EMERGENCY ALERT!\nI may be in danger. Please contact me immediately."

        AppState.trustedContacts.forEach { number ->
            try {
                smsManager.sendTextMessage(number, null, message, null, null)
            } catch (e: Exception) {
                ThreatLogger.logSystem("SOS SMS failed to $number")
            }
        }

        ThreatLogger.logSystem("🚨 SOS SMS sent to trusted contacts")
    }
}

@Composable
fun SOSScreen(onDismiss: () -> Unit) {

    val isHindi = LanguageManager.isHindi()

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB71C1C))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red.copy(alpha = alpha * 0.3f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(Modifier.height(40.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = if (isHindi) "आपातकालीन स्थिति!" else "EMERGENCY ACTIVE",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi)
                            "आपके विश्वसनीय संपर्कों को सूचित किया जा रहा है।"
                        else
                            "Alerting your trusted contacts now.",
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = if (isHindi)
                            "शांत रहें, मदद रास्ते में है।"
                        else
                            "Stay calm. Help is on the way.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFB71C1C)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (isHindi) "मैं सुरक्षित हूँ" else "I AM SAFE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}