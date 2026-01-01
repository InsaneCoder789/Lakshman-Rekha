package com.lakshmanrekha.protect.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName ?: return
        val message = sbn.notification.extras.getCharSequence("android.text")?.toString()

        if (message != null) {
            Log.d("LakshmanRekha", "📩 From: $packageName → $message")

            if (containsScamKeywords(message)) {
                onScamDetected(packageName, message)
            }
        }
    }

    // 🔍 Scam signal detection layer
    private fun containsScamKeywords(text: String): Boolean {
        val keywords = listOf(
            "otp", "o t p", "one time password",
            "kyc", "refund", "urgent", "blocked", "verify",
            "account", "lottery", "click", "qr", "upi", "password",
            "activation", "due", "payment request", "verification"
        )
        return keywords.any { text.contains(it, ignoreCase = true) }
    }

    // 🚨 When a scam is detected → Trigger Overlay Popup
    private fun onScamDetected(from: String, message: String) {
        Log.w("LakshmanRekha", "⚠️ SCAM DETECTED → $from :: $message")

        val overlayText = """
            ⚠️ सावधान / WARNING
            "$message"
            👉 OTP या बैंक की जानकारी साझा न करें।
            📞 किसी विश्वसनीय व्यक्ति से बात करें।
        """.trimIndent()

        val intent = Intent(this, OverlayService::class.java)
        intent.putExtra("overlay_message", overlayText)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startService(intent)
    }
}