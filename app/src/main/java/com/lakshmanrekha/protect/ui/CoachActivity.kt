package com.lakshmanrekha.protect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme

class CoachActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LakshmanRekhaTheme {
                CoachScreen(
                    onDone = {
                        finish() // close coach after user understands
                    }
                )
            }
        }
    }
}