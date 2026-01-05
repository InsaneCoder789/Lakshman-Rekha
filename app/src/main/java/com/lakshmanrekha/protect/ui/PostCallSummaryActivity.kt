package com.lakshmanrekha.protect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme

class PostCallSummaryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val risk = intent.getIntExtra("risk", 0)
        val reasons = intent.getStringArrayListExtra("reasons") ?: arrayListOf()

        setContent {
            LakshmanRekhaTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    Text("Call Risk Summary", style = MaterialTheme.typography.headlineMedium)

                    Spacer(Modifier.height(12.dp))

                    Text("Risk Score: $risk / 100")

                    Spacer(Modifier.height(16.dp))

                    reasons.forEach {
                        Text("• $it")
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(onClick = { finish() }) {
                        Text("Done")
                    }
                }
            }
        }
    }
}