package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.ProtectionMode

@Composable
fun ProfileSetupScreen(
    onComplete: (name: String, age: Int, mode: ProtectionMode) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(ProtectionMode.SAATHI) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Welcome", fontSize = 26.sp)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Protection Mode", fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            ProtectionMode.values()
                .filter { it != ProtectionMode.NONE }
                .forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode }
                        )
                        Text(mode.name)
                    }
                }

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (name.isNotBlank() && age.isNotBlank()) {
                        onComplete(
                            name.trim(),
                            age.toInt(),
                            selectedMode
                        )
                    }
                }
            ) {
                Text("Continue")
            }
        }
    }
}