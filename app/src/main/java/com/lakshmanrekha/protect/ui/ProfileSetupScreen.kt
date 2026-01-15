package com.lakshmanrekha.protect.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.ProtectionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onComplete: (name: String, age: Int, mode: ProtectionMode) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(ProtectionMode.SAATHI) }

    val isHindi = LanguageManager.isHindi()
    val isFormValid = name.isNotBlank() && age.isNotBlank() && (age.toIntOrNull() ?: 0) > 0

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(48.dp))

            // --- HEADER ---
            Text(
                text = if (isHindi) "अपना प्रोफाइल भरें" else "Setup Your Profile",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            )
            Text(
                text = if (isHindi) "यह जानकारी आपको सुरक्षित रखने में मदद करेगी।" else "This helps us personalize your protection.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(32.dp))

            // --- INPUT FIELDS ---
            Text(
                text = if (isHindi) "आपका नाम" else "Full Name",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(if (isHindi) "उदा. राजेश कुमार" else "e.g. John Doe") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Person, null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (isHindi) "आपकी उम्र" else "Your Age",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() }.take(3) },
                placeholder = { Text("00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(140.dp),
                leadingIcon = { Icon(Icons.Rounded.Cake, null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(32.dp))

            // --- MODE SELECTION ---
            Text(
                text = if (isHindi) "सुरक्षा का स्तर चुनें" else "Select Protection Level",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(Modifier.height(12.dp))

            ModeSelectionTile(
                mode = ProtectionMode.RAKSHA,
                title = if (isHindi) "लक्ष्मण मोड (Lakshman)" else "Lakshman Mode",
                subtitle = if (isHindi) "सबसे कठोर सुरक्षा" else "Maximum Security",
                icon = Icons.Rounded.Shield,
                color = Color(0xFFD32F2F),
                isSelected = selectedMode == ProtectionMode.RAKSHA,
                onSelect = { selectedMode = ProtectionMode.RAKSHA }
            )

            ModeSelectionTile(
                mode = ProtectionMode.LAKSHMAN,
                title = if (isHindi) "रक्षा मोड (Raksha)" else "Raksha Mode",
                subtitle = if (isHindi) "स्मार्ट और संतुलित" else "Smart & Balanced",
                icon = Icons.Rounded.GppGood,
                color = Color(0xFFF57C00),
                isSelected = selectedMode == ProtectionMode.LAKSHMAN,
                onSelect = { selectedMode = ProtectionMode.LAKSHMAN }
            )

            ModeSelectionTile(
                mode = ProtectionMode.SAATHI,
                title = if (isHindi) "साथी मोड (Saathi)" else "Saathi Mode",
                subtitle = if (isHindi) "सिर्फ टिप्स और मदद" else "Tips & Guidance",
                icon = Icons.Rounded.Handshake,
                color = Color(0xFF1976D2),
                isSelected = selectedMode == ProtectionMode.SAATHI,
                onSelect = { selectedMode = ProtectionMode.SAATHI }
            )

            Spacer(Modifier.height(40.dp))

            // --- SAVE BUTTON ---
            Button(
                onClick = { if (isFormValid) onComplete(name.trim(), age.toInt(), selectedMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = isFormValid,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (isHindi) "प्रोफाइल सुरक्षित करें" else "Save & Start Protection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ModeSelectionTile(
    mode: ProtectionMode,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderWidth by animateDpAsState(if (isSelected) 3.dp else 1.dp, label = "borderWidth")
    val borderColor by animateColorAsState(if (isSelected) color else MaterialTheme.colorScheme.outlineVariant, label = "borderColor")
    val containerColor by animateColorAsState(if (isSelected) color.copy(alpha = 0.08f) else Color.Transparent, label = "bgColor")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(borderWidth, borderColor),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) color else color.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (isSelected) Color.White else color
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = color)
            )
        }
    }
}