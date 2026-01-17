package com.lakshmanrekha.protect.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.*

/* ----------------------------------------------------
 * NAVIGATION STATES (UNCHANGED)
 * ---------------------------------------------------- */
sealed class ActiveScreen {
    object Home : ActiveScreen()
    object Contacts : ActiveScreen()
    object Activity : ActiveScreen()
}

/* ----------------------------------------------------
 * ROOT
 * ---------------------------------------------------- */
@Composable
fun HomeConsoleScreen() {
    var currentScreen by remember { mutableStateOf<ActiveScreen>(ActiveScreen.Home) }

    when (currentScreen) {
        ActiveScreen.Contacts ->
            TrustedContactsScreen(onDone = { currentScreen = ActiveScreen.Home })

        ActiveScreen.Activity ->
            RecentActivityContainer(onBack = { currentScreen = ActiveScreen.Home })

        ActiveScreen.Home ->
            HomeScreenContent(onNavigate = { currentScreen = it })
    }
}

/* ----------------------------------------------------
 * HOME CONTENT (UNCHANGED)
 * ---------------------------------------------------- */
@Composable
private fun HomeScreenContent(onNavigate: (ActiveScreen) -> Unit) {

    val context = LocalContext.current
    val systemLogs = ThreatLogger.getSystemLogs()
    val isHindi = LanguageManager.isHindi()

    val currentMode = AppState.protectionMode
    val activeBrandColor by animateColorAsState(
        targetValue = getThemeColorForMode(currentMode),
        animationSpec = tween(700),
        label = "backgroundTransition"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(40000, easing = LinearEasing)
        ),
        label = "rotate"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = activeBrandColor) {
        Box(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                        )
                    )
            )

            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(600.dp)
                    .align(Alignment.Center)
                    .rotate(rotation)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                item {
                    Column(modifier = Modifier.padding(top = 40.dp)) {
                        Text(
                            text = Strings.greeting(AppState.name),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isHindi)
                                "आपकी सुरक्षा सक्रिय है"
                            else
                                "Your perimeter is fully active",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp
                        )
                    }
                }

                item { StatusCard(currentMode, activeBrandColor) }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = if (isHindi)
                                "सुरक्षा का स्तर चुनें"
                            else
                                "Set Protection Level",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        ProtectionModeSelector(context)
                    }
                }

                item {
                    TrustedContactsCard(
                        onManageContacts = { onNavigate(ActiveScreen.Contacts) }
                    )
                }

                item {
                    ActivityEntryCard(
                        isHindi,
                        onNavigate = { onNavigate(ActiveScreen.Activity) }
                    )
                }

                item { CoachEntryCard(context, isHindi) }

                item {
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

/* ----------------------------------------------------
 * RECENT ACTIVITY
 * ---------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActivityContainer(onBack: () -> Unit) {

    val threats = ThreatLogger.getThreats()
    val isHindi = LanguageManager.isHindi()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isHindi) "सुरक्षा गतिविधि" else "Security Activity",
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (threats.isEmpty()) {
                item { EmptyThreatState() }
            } else {
                itemsIndexed(threats) { _, threat ->
                    ThreatItem(threat)
                }
            }
        }
    }
}

/* ----------------------------------------------------
 * 🔥 UPDATED THREAT ITEM (NEW ACTIONS ADDED)
 * ---------------------------------------------------- */
@Composable
private fun ThreatItem(threat: Threat) {

    val baseColor = when (threat.level) {
        ThreatLevel.SAFE -> Color(0xFF2E7D32)
        ThreatLevel.CAUTION -> Color(0xFFE59400)
        ThreatLevel.RISKY -> Color(0xFFD84315)
        ThreatLevel.DANGEROUS -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = baseColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Radar,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = threat.level.name,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    threat.reasons.forEach {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            /* ✅ NEW: ACTIONS ONLY FOR RISKY & DANGEROUS */
            if (
                threat.level == ThreatLevel.RISKY ||
                threat.level == ThreatLevel.DANGEROUS
            ) {
                Spacer(Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.25f))
                ThreatActionRow(
                    threat = threat,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

/* ----------------------------------------------------
 * REMAINING COMPONENTS (UNCHANGED)
 * ---------------------------------------------------- */

@Composable
private fun StatusCard(mode: ProtectionMode, brandColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Surface(modifier = Modifier.size(70.dp).alpha(pulseAlpha), shape = CircleShape, color = Color.White.copy(alpha = 0.4f)) {}
                Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Color.White) {
                    Icon(imageVector = Icons.Rounded.Security, contentDescription = null, tint = brandColor, modifier = Modifier.padding(12.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(text = if (LanguageManager.isHindi()) "सुरक्षा कवच" else "SHIELD ACTIVE", fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.6f))
                Text(text = mode.name, fontWeight = FontWeight.Black, color = Color.White, fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun ActivityEntryCard(isHindi: Boolean, onNavigate: () -> Unit) {
    Card(
        onClick = onNavigate,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.History, null, tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = if (isHindi) "हाल की गतिविधि" else "Recent Activity", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
                Text(text = if (isHindi) "सुरक्षा लॉग और अलर्ट" else "View security logs & alerts", fontSize = 15.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun CoachEntryCard(context: android.content.Context, isHindi: Boolean) {
    Card(
        onClick = { CoachLauncher.launch(context) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.18f))
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = if (isHindi) "सुरक्षा गाइड" else "Senior Safety Guide", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
                Text(text = if (isHindi) "धोखाधड़ी से कैसे बचें" else "Learn to spot phone scams", fontSize = 15.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun EmptyThreatState() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.VerifiedUser, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(60.dp))
        Spacer(Modifier.height(12.dp))
        Text(text = Strings.noThreats(), color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
    }
}



private fun getThemeColorForMode(mode: ProtectionMode): Color =
    when (mode) {
        ProtectionMode.SAATHI -> Color(0xFF0D5ED4)   // Sleek Sapphire
        ProtectionMode.RAKSHA -> Color(0xFFE59400)   // Deep Amber
        ProtectionMode.LAKSHMAN -> Color(0xFFD32F2F) // Blood Red
        else -> Color(0xFF0D5ED4)
    }
