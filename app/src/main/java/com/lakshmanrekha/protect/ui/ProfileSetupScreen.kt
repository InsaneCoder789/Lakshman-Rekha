package com.lakshmanrekha.protect.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
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

    // --- THEME COLORS ---
    val lakshmanBlue = Color(0xFF0D47A1)
    val lakshmanDarkNavy = Color(0xFF0A192F)
    val lakshmanAccent = Color(0xFF4FC3F7)
    val bgGradient = Brush.verticalGradient(listOf(lakshmanDarkNavy, lakshmanBlue))

    Surface(modifier = Modifier.fillMaxSize(), color = lakshmanDarkNavy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(56.dp))

            // --- HEADER ---
            Text(
                text = if (isHindi) "अपना प्रोफाइल भरें" else "Setup Your Profile",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 34.sp,
                    letterSpacing = (-1).sp
                )
            )
            Text(
                text = if (isHindi) "यह जानकारी आपको सुरक्षित रखने में मदद करेगी।" else "This helps us personalize your protection.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(40.dp))

            // --- NAME INPUT ---
            Text(
                text = if (isHindi) "आपका नाम" else "Full Name",
                color = lakshmanAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(if (isHindi) "उदा. राजेश कुमार" else "e.g. John Doe", color = Color.White.copy(0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Person, null, tint = Color.White) },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = lakshmanAccent,
                    unfocusedBorderColor = Color.White.copy(0.2f),
                    unfocusedContainerColor = Color.White.copy(0.05f),
                    focusedContainerColor = Color.White.copy(0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(24.dp))

            // --- AGE INPUT ---
            Text(
                text = if (isHindi) "आपकी उम्र" else "Your Age",
                color = lakshmanAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() }.take(3) },
                placeholder = { Text("00", color = Color.White.copy(0.4f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(160.dp),
                leadingIcon = { Icon(Icons.Rounded.Cake, null, tint = Color.White) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = lakshmanAccent,
                    unfocusedBorderColor = Color.White.copy(0.2f),
                    unfocusedContainerColor = Color.White.copy(0.05f),
                    focusedContainerColor = Color.White.copy(0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(40.dp))

            // --- PROTECTION LEVEL SELECTION ---
            Text(
                text = if (isHindi) "सुरक्षा का स्तर चुनें" else "Protection Level",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 24.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            ModeSelectionTile(
                title = if (isHindi) "लक्ष्मण मोड (Lakshman)" else "Lakshman Mode",
                subtitle = if (isHindi) "सबसे कठोर सुरक्षा" else "Maximum Security",
                icon = Icons.Rounded.Shield,
                color = Color(0xFFF44336), // Vibrant Red
                isSelected = selectedMode == ProtectionMode.LAKSHMAN,
                onSelect = { selectedMode = ProtectionMode.LAKSHMAN }
            )

            ModeSelectionTile(
                title = if (isHindi) "रक्षा मोड (Raksha)" else "Raksha Mode",
                subtitle = if (isHindi) "स्मार्ट और संतुलित" else "Smart & Balanced",
                icon = Icons.Rounded.GppGood,
                color = Color(0xFFFF9800), // Vibrant Orange
                isSelected = selectedMode == ProtectionMode.RAKSHA,
                onSelect = { selectedMode = ProtectionMode.RAKSHA }
            )

            ModeSelectionTile(
                title = if (isHindi) "साथी मोड (Saathi)" else "Saathi Mode",
                subtitle = if (isHindi) "सिर्फ टिप्स और मदद" else "Tips & Guidance",
                icon = Icons.Rounded.Handshake,
                color = Color(0xFF2196F3), // Vibrant Blue
                isSelected = selectedMode == ProtectionMode.SAATHI,
                onSelect = { selectedMode = ProtectionMode.SAATHI }
            )

            Spacer(Modifier.height(48.dp))

            // --- SAVE BUTTON ---
            Button(
                onClick = { if (isFormValid) onComplete(name.trim(), age.toInt(), selectedMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = lakshmanBlue,
                    disabledContainerColor = Color.White.copy(alpha = 0.2f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
            ) {
                Text(
                    text = if (isHindi) "प्रोफाइल सुरक्षित करें" else "Save & Start Protection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ModeSelectionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderWidth by animateDpAsState(if (isSelected) 2.dp else 1.dp, label = "borderWidth")
    val borderColor by animateColorAsState(if (isSelected) color else Color.White.copy(alpha = 0.1f), label = "borderColor")
    val containerColor by animateColorAsState(if (isSelected) color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f), label = "bgColor")

    Surface(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(borderWidth, borderColor),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = if (isSelected) color else Color.White.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (isSelected) Color.White else color
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.5f)
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}