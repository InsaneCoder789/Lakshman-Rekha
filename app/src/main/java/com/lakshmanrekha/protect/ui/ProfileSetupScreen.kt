package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val isFormValid = name.isNotBlank() && age.isNotBlank()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = if (isHindi) "अपना प्रोफाइल बनाएं" else "Create Profile",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )
            )

            Spacer(Modifier.height(32.dp))

            // Name Input
            Text(
                text = if (isHindi) "आपका नाम" else "Your Name",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(if (isHindi) "नाम लिखें" else "Type your name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
            )

            Spacer(Modifier.height(24.dp))

            // Age Input
            Text(
                text = if (isHindi) "आपकी उम्र" else "Your Age",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() }.take(3) },
                placeholder = { Text("00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = if (isHindi) "सुरक्षा मोड चुनें" else "Choose Protection Mode",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            // Selection Tiles (Senior Friendly)
            ProtectionMode.entries
                .filter { it != ProtectionMode.NONE }
                .forEach { mode ->
                    val isSelected = selectedMode == mode
                    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = { selectedMode = mode }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(if (isSelected) 3.dp else 1.dp, borderColor),
                        color = containerColor
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMode = mode }
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = mode.name.lowercase().capitalize(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 22.sp
                                )
                            )
                        }
                    }
                }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isFormValid,
                onClick = {
                    if (isFormValid) {
                        onComplete(name.trim(), age.toInt(), selectedMode)
                    }
                }
            ) {
                Text(
                    text = if (isHindi) "प्रोफाइल सुरक्षित करें" else "Save Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}