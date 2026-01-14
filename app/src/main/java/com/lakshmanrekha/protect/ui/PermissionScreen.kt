package com.lakshmanrekha.protect.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.PermissionUtils // ✅ IMPORT THIS

@Composable
fun PermissionScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val isHindi = LanguageManager.isHindi()

    val bgBlue = Color(0xFF0D47A1)
    val cardSurface = Color(0xFF1565C0)

    // ✅ FIXED: Calling functions via PermissionUtils object
    var notifGranted by remember {
        mutableStateOf(PermissionUtils.isNotificationAccessEnabled(context))
    }
    var accessibilityGranted by remember {
        mutableStateOf(PermissionUtils.isAccessibilityServiceEnabled(context))
    }

    // Polling logic to detect when user returns from Settings
    LaunchedEffect(Unit) {
        while (true) {
            notifGranted = PermissionUtils.isNotificationAccessEnabled(context)
            accessibilityGranted = PermissionUtils.isAccessibilityServiceEnabled(context)
            kotlinx.coroutines.delay(1000)
        }
    }

    val canContinue = notifGranted && accessibilityGranted

    // Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "pulse"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = bgBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(120.dp)) {
                Box(
                    Modifier
                        .size(90.dp)
                        .alpha(0.2f)
                        .background(Color.White, CircleShape)
                        .padding((pulseScale * 5).dp)
                )
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = if (canContinue) Icons.Rounded.VerifiedUser else Icons.Rounded.Security,
                        contentDescription = null,
                        tint = bgBlue,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = if (isHindi) "सुरक्षा सेटअप" else "Safety Setup",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black, color = Color.White
                )
            )

            Text(
                text = if (isHindi)
                    "लक्ष्मण रेखा को सक्रिय करने के लिए ये अनुमतियाँ दें"
                else "Grant these to activate your Lakshman Rekha.",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(44.dp))

            // Message Protection Card
            PermissionTile(
                title = if (isHindi) "मैसेज सुरक्षा" else "Message Protection",
                desc = if (isHindi) "स्कैम SMS को रोकने के लिए" else "Scans SMS for scam links",
                granted = notifGranted,
                cardColor = cardSurface,
                onEnable = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
            )

            Spacer(Modifier.height(16.dp))

            // Call/Screen Protection Card
            PermissionTile(
                title = if (isHindi) "कॉल सुरक्षा" else "Smart Guard",
                desc = if (isHindi) "धोखाधड़ी वाली ऐप्स को रोकने के लिए" else "Blocks malicious app overlays",
                granted = accessibilityGranted,
                cardColor = cardSurface,
                onEnable = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )

            Spacer(Modifier.weight(1f))

            Button(
                enabled = canContinue,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = bgBlue,
                    disabledContainerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = if (isHindi) "सुरक्षा शुरू करें" else "Activate Shield",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun PermissionTile(
    title: String,
    desc: String,
    granted: Boolean,
    cardColor: Color,
    onEnable: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (granted) Color(0xFF2E7D32) else cardColor
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (granted) Icons.Rounded.Verified else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (granted) Color.White else Color.White.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White)
                Text(desc, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }
            if (!granted) {
                TextButton(
                    onClick = onEnable,
                    colors = ButtonDefaults.textButtonColors(containerColor = Color.White)
                ) {
                    Text("Enable", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}