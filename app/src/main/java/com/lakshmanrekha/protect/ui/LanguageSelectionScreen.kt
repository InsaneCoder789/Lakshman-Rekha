package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.AppLanguage

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Choose Language\nभाषा चुनें",
                fontSize = 28.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onLanguageSelected(AppLanguage.ENGLISH) }
            ) {
                Text("English")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onLanguageSelected(AppLanguage.HINDI) }
            ) {
                Text("हिंदी")
            }
        }
    }
}