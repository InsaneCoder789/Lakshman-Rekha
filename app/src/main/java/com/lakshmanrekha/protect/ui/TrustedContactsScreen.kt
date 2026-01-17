package com.lakshmanrekha.protect.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val isHindi = LanguageManager.isHindi()

    // --- THEME COLORS ---
    val lakshmanBlue = Color(0xFF0D47A1)
    val lakshmanDarkNavy = Color(0xFF0A192F)
    val lakshmanAccent = Color(0xFF4FC3F7)
    val bgGradient = Brush.verticalGradient(listOf(lakshmanDarkNavy, Color(0xFF0D47A1)))

    var allContacts by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selected by remember { mutableStateOf(AppState.trustedContacts.toSet()) }
    var isLoading by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = remember(searchQuery, allContacts) {
        if (searchQuery.isBlank()) allContacts
        else allContacts.filter {
            it.first.contains(searchQuery, ignoreCase = true) || it.second.contains(searchQuery)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) { permissionDenied = false; isLoading = true }
        else { permissionDenied = true; isLoading = false }
    }

    LaunchedEffect(isLoading) {
        if (isLoading && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            allContacts = loadContactsSafe(context)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            isLoading = true
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    Scaffold(
        containerColor = lakshmanDarkNavy,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        text = if (isHindi) "भरोसेमंद संपर्क" else "Protection Circle",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White
                        )
                    )
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.background(lakshmanBlue).navigationBarsPadding()) {
                Button(
                    onClick = {
                        AppState.trustedContacts = selected.toMutableSet()
                        AppState.hasAddedTrustedContacts = true
                        AppPrefs.saveTrustedContacts(context, selected)
                        AppPrefs.save(context)
                        onDone()
                    },
                    modifier = Modifier.fillMaxWidth().padding(24.dp).height(72.dp),
                    enabled = selected.isNotEmpty(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = lakshmanBlue,
                        disabledContainerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = if (isHindi) "सहेजें और जारी रखें" else "Secure These Contacts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(bgGradient)) {

            // --- 1. SELECTED TRAY (GLASS STYLE) ---
            AnimatedVisibility(visible = selected.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = if (isHindi) "सुरक्षित घेरे में" else "In Your Safety Circle",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = lakshmanAccent, fontWeight = FontWeight.Black
                        )
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(selected.toList()) { number ->
                            val name = allContacts.find { it.second == number }?.first ?: number
                            SelectedChip(name, Color.White) { selected = selected - number }
                        }
                    }
                }
            }

            // --- 2. SEARCH BAR (MODERN) ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                placeholder = { Text(if (isHindi) "नाम खोजें..." else "Search contacts...", color = Color.White.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = lakshmanAccent,
                    unfocusedBorderColor = Color.White.copy(0.2f),
                    unfocusedContainerColor = Color.White.copy(0.05f),
                    focusedContainerColor = Color.White.copy(0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            // --- 3. CONTACT LIST (HIGH DEPTH CARDS) ---
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color.White)
                    permissionDenied -> PermissionError(Color.White) { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredContacts, key = { it.second }) { (name, number) ->
                                ModernContactCard(
                                    name = name,
                                    number = number,
                                    isSelected = selected.contains(number),
                                    onToggle = {
                                        selected = if (selected.contains(number)) selected - number else selected + number
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedChip(name: String, themeColor: Color, onRemove: () -> Unit) {
    Surface(
        onClick = onRemove,
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name.take(12), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.Cancel, null, modifier = Modifier.size(18.dp), tint = Color.White)
        }
    }
}

@Composable
private fun ModernContactCard(
    name: String,
    number: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val lakshmanAccent = Color(0xFF4FC3F7)

    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = if (isSelected) Color.White.copy(0.15f) else Color.White.copy(0.05f),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) lakshmanAccent else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) lakshmanAccent else Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Rounded.Shield, null, tint = Color.White, modifier = Modifier.size(28.dp))
                } else {
                    Text(
                        name.take(1).uppercase(),
                        fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.White
                    )
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    name,
                    fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = Color.White,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    number,
                    fontSize = 15.sp, color = Color.White.copy(0.6f), fontWeight = FontWeight.Medium
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = lakshmanAccent,
                    uncheckedColor = Color.White.copy(0.3f),
                    checkmarkColor = Color(0xFF0D47A1)
                )
            )
        }
    }
}

@Composable
private fun PermissionError(themeColor: Color, onGrant: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.Contacts, null, modifier = Modifier.size(100.dp), tint = Color.White.copy(alpha = 0.2f))
        Spacer(Modifier.height(24.dp))
        Text(
            text = if (LanguageManager.isHindi()) "संपर्क अनुमति आवश्यक है" else "Contacts Access Needed",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = Color.White),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onGrant,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0D47A1))
        ) {
            Text("Allow Access", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

private suspend fun loadContactsSafe(context: Context): List<Pair<String, String>> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Pair<String, String>>()
    context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
        null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val name = cursor.getString(0) ?: continue
            val number = cursor.getString(1)?.replace("\\s".toRegex(), "") ?: continue
            if (number.length >= 6) result.add(name to number)
        }
    }
    result.distinctBy { it.second }
}