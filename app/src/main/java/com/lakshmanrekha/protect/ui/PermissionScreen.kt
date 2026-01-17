package com.lakshmanrekha.protect.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.PermissionUtils
import kotlinx.coroutines.delay

@Composable
fun PermissionScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val isHindi = LanguageManager.isHindi()

    // --- ELITE SAPPHIRE PALETTE ---
    val sapphireBlack = Color(0xFF020408)
    val sapphirePrimary = Color(0xFF0D5ED4)
    val sapphireAccent = Color(0xFF4FC3F7)
    val successGreen = Color(0xFF00E676)
    val errorRed = Color(0xFFFF3D00)

    val mainBgGradient = Brush.verticalGradient(
        0.0f to sapphirePrimary.copy(alpha = 0.25f),
        0.4f to sapphireBlack,
        1.0f to sapphireBlack
    )

    val overlaySupported = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.M }

    /* --- LOGIC (UNTOUCHED) --- */
    var notifGranted by remember { mutableStateOf(PermissionUtils.isNotificationAccessEnabled(context)) }
    var accessibilityGranted by remember { mutableStateOf(PermissionUtils.isAccessibilityServiceEnabled(context)) }
    var overlayGranted by remember { mutableStateOf(if (overlaySupported) Settings.canDrawOverlays(context) else false) }

    LaunchedEffect(Unit) {
        while (true) {
            notifGranted = PermissionUtils.isNotificationAccessEnabled(context)
            accessibilityGranted = PermissionUtils.isAccessibilityServiceEnabled(context)
            if (overlaySupported) overlayGranted = Settings.canDrawOverlays(context)
            delay(1000)
        }
    }

    val canContinue = notifGranted && accessibilityGranted && (if (overlaySupported) overlayGranted else true)

    Surface(modifier = Modifier.fillMaxSize(), color = sapphireBlack) {
        Box(modifier = Modifier.fillMaxSize().background(mainBgGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))

                // --- 1. THE CINEMATIC SHIELD HUB ---
                ShieldPowerCore(
                    isActive = canContinue,
                    notif = notifGranted,
                    accessibility = accessibilityGranted,
                    overlay = if (overlaySupported) overlayGranted else true,
                    accentColor = sapphireAccent,
                    successColor = successGreen,
                    errorColor = errorRed
                )

                Spacer(Modifier.height(56.dp))

                // --- 2. TYPOGRAPHY (PERFECTLY ALIGNED) ---
                Text(
                    text = "SYSTEM INTEGRITY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = sapphireAccent.copy(alpha = 0.7f),
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isHindi) "सुरक्षा कवच सक्रिय करें" else "Establish Perimeter",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isHindi) "सभी अनुमतियाँ चालू करें" else "Authorize all protocols to secure your device",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                )

                // --- 3. THE TILES (GLASS-REFRACTION STYLE) ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModernGlassTile(
                        title = if (isHindi) "मैसेज सुरक्षा" else "Neural Guard",
                        desc = if (isHindi) "धोखाधड़ी संदेश फिल्टर" else "Real-time AI SMS & link scanning",
                        icon = Icons.Rounded.SmsFailed,
                        granted = notifGranted,
                        successColor = successGreen,
                        onEnable = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                    )

                    ModernGlassTile(
                        title = if (isHindi) "स्क्रीन सुरक्षा" else "Behavior Link",
                        desc = if (isHindi) "ऐप गतिविधियों की पहचान" else "Identifies malicious application patterns",
                        icon = Icons.Rounded.ScreenLockPortrait,
                        granted = accessibilityGranted,
                        successColor = successGreen,
                        onEnable = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                    )

                    if (overlaySupported) {
                        ModernGlassTile(
                            title = if (isHindi) "आपात ओवरले" else "Active Overlay",
                            desc = if (isHindi) "चेतावनी संदेश डिस्प्ले" else "Renders critical threat warnings",
                            icon = Icons.Rounded.WarningAmber,
                            granted = overlayGranted,
                            successColor = successGreen,
                            onEnable = {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(Modifier.height(40.dp))

                // --- 4. THE POWER BUTTON ---
                Button(
                    onClick = onContinue,
                    enabled = canContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .shadow(
                            elevation = if (canContinue) 30.dp else 0.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = successGreen,
                            spotColor = successGreen
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canContinue) Color.White else Color.White.copy(alpha = 0.05f),
                        contentColor = sapphireBlack,
                        disabledContainerColor = Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Icon(
                        imageVector = if (canContinue) Icons.Rounded.GppGood else Icons.Rounded.LockOpen,
                        modifier = Modifier.size(24.dp),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (canContinue) (if (isHindi) "सुरक्षा कवच शुरू करें" else "ACTIVATE DEFENSES")
                        else (if (isHindi) "अनुमति आवश्यक" else "LOCKED"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun ShieldPowerCore(
    isActive: Boolean,
    notif: Boolean,
    accessibility: Boolean,
    overlay: Boolean,
    accentColor: Color,
    successColor: Color,
    errorColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.size(240.dp).offset(y = floatAnim.dp), contentAlignment = Alignment.Center) {
        // Outer Core Glow
        Box(
            modifier = Modifier
                .size(170.dp)
                .scale(pulse)
                .background(
                    Brush.radialGradient(
                        listOf((if (isActive) successColor else accentColor).copy(alpha = 0.2f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Glass Disk
        Surface(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            color = if (isActive) successColor else Color.White.copy(alpha = 0.08f),
            border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent))),
            shadowElevation = 20.dp
        ) {
            Icon(
                imageVector = if (isActive) Icons.Rounded.VerifiedUser else Icons.Rounded.Security,
                tint = Color.White,
                modifier = Modifier.padding(30.dp),
                contentDescription = null
            )
        }

        // Orbitals
        OrbitalStatus(Modifier.align(Alignment.TopCenter).offset(y = (-10).dp), notif, successColor, errorColor)
        OrbitalStatus(Modifier.align(Alignment.BottomStart).offset(x = 10.dp, y = (-5).dp), accessibility, successColor, errorColor)
        OrbitalStatus(Modifier.align(Alignment.BottomEnd).offset(x = (-10).dp, y = (-5).dp), overlay, successColor, errorColor)
    }
}

@Composable
private fun OrbitalStatus(modifier: Modifier, active: Boolean, success: Color, error: Color) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (active) success else error)
            .border(4.dp, Color(0xFF020408), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (active) Icons.Rounded.Check else Icons.Rounded.Close,
            tint = Color.White, modifier = Modifier.size(20.dp), contentDescription = null
        )
    }
}

@Composable
private fun ModernGlassTile(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    granted: Boolean,
    successColor: Color,
    onEnable: () -> Unit
) {
    val borderColor = if (granted) successColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)

    Surface(
        onClick = { if (!granted) onEnable() },
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (granted) successColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.04f),
        border = BorderStroke(if (granted) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(if (granted) successColor else Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                Text(desc, fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f), lineHeight = 16.sp)
            }

            Icon(
                imageVector = if (granted) Icons.Rounded.CheckCircle else Icons.Rounded.ChevronRight,
                tint = if (granted) successColor else Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(28.dp),
                contentDescription = null
            )
        }
    }
}