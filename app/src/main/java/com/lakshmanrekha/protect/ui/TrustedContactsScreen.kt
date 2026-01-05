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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val isHindi = LanguageManager.isHindi()

    var allContacts by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selected by remember { mutableStateOf(AppState.trustedContacts.toSet()) }
    var isLoading by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Logic for filtering contacts based on search query
    val filteredContacts = remember(searchQuery, allContacts) {
        if (searchQuery.isBlank()) allContacts
        else allContacts.filter {
            it.first.contains(searchQuery, ignoreCase = true) || it.second.contains(searchQuery)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) { isLoading = true; permissionDenied = false }
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

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(32.dp))

            // --- HEADER ---
            Text(
                text = if (isHindi) "विश्वसनीय संपर्क चुनें" else "Trusted Contacts",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (isHindi) "आपात स्थिति में इन्हें अलर्ट भेजा जाएगा।" else "Emergency alerts will be sent to them.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(20.dp))

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isHindi) "नाम या नंबर खोजें" else "Search name or number") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(12.dp))

            // --- SELECTION CHIP ---
            AnimatedVisibility(visible = selected.isNotEmpty()) {
                InputChip(
                    selected = true,
                    onClick = { },
                    label = { Text("${selected.size} " + (if (isHindi) "चयनित" else "Selected")) },
                    trailingIcon = { Icon(Icons.Rounded.CheckCircle, null, Modifier.size(18.dp)) },
                    colors = InputChipDefaults.inputChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }

            // --- CONTACT LIST AREA ---
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    permissionDenied -> PermissionError(onGrant = {
                        permissionDenied = false
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    })
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredContacts, key = { it.second }) { (name, number) ->
                                ContactCard(
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

            // --- ACTION BUTTON ---
            Button(
                onClick = {
                    AppState.trustedContacts = selected.toMutableSet()
                    AppState.hasAddedTrustedContacts = true
                    AppPrefs.saveTrustedContacts(context, selected)
                    AppPrefs.save(context)
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth().height(60.dp).padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = selected.isNotEmpty()
            ) {
                Text(if (isHindi) "सुरक्षित करें और जारी रखें" else "Save & Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ContactCard(name: String, number: String, isSelected: Boolean, onToggle: () -> Unit) {
    val borderColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
    val bgColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)

    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Color.White)
                } else {
                    Text(name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Text(number, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun PermissionError(onGrant: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (LanguageManager.isHindi()) "अनुमति आवश्यक है" else "Permission Required", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGrant) { Text(if (LanguageManager.isHindi()) "अनुमति दें" else "Grant Access") }
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