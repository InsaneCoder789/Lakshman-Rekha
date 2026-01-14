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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    val bgBlue = Color(0xFF0D47A1)
    val sosRed = Color(0xFFB71C1C)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Background Rotation for the Shield
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "rotate"
    )

    // Intense Red Pulse
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "alpha"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgBlue) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Branded Watermark
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(480.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            // Emergency Red Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(sosRed.copy(alpha = alpha))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.height(40.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Pulsing Warning Icon
                    Box(contentAlignment = Alignment.Center) {
                        Box(Modifier.size(120.dp).alpha(alpha).background(Color.White, CircleShape))
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 12.dp
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = sosRed,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = if (isHindi) "आपातकालीन स्थिति!" else "EMERGENCY ACTIVE",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "सुरक्षा घेरा सक्रिय" else "Protection Active",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "आपके संपर्कों को सूचित किया जा रहा है।" else "Alerting your trusted contacts now.",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = if (isHindi) "शांत रहें, मदद रास्ते में है।" else "Stay calm. Help is on the way.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = sosRed
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = if (isHindi) "मैं सुरक्षित हूँ" else "I AM SAFE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSOS() {
    SOSScreen(onDismiss = {})
}