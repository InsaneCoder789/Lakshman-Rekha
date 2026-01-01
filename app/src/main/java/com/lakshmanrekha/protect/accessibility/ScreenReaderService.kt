package com.lakshmanrekha.protect.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class ScreenReaderService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val text = event.text.joinToString(" ") { it.toString() }

        // Only process meaningful text
        if (text.isNotBlank()) {
            Log.d("LakshmanRekha-Screen", "🖥️ On Screen: $text")

            if (containsScamPattern(text)) {
                onScamDetected(text)
            }
        }
    }

    override fun onInterrupt() {
        Log.w("LakshmanRekha-Screen", "⚠️ Service Interrupted.")
    }

    private fun containsScamPattern(text: String): Boolean {
        val patterns = listOf(
            "otp", "o t p", "one time password",
            "kyc", "refund", "qr code", "payment request",
            "blocked", "verify now", "activate account",
            "urgent", "pending payment"
        )
        return patterns.any { text.contains(it, ignoreCase = true) }
    }

    private fun onScamDetected(text: String) {
        Log.e("LakshmanRekha-Screen", "🚨 SCAM TEXT DETECTED ON SCREEN → $text")
        // 🚧 TODO: Trigger Overlay Warning Next
    }
}