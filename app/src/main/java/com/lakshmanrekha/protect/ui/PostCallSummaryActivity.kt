package com.lakshmanrekha.protect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme
import com.lakshmanrekha.protect.utils.RuntimeState

class PostCallSummaryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LakshmanRekhaTheme {
                PostCallSummaryScreen {
                    RuntimeState.lastCallThreatLevel = null
                    RuntimeState.lastCallReasons = emptyList()
                    finish()
                }
            }
        }
    }
}