package com.lakshmanrekha.protect.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lakshmanrekha.protect.core.ProtectionManager
import com.lakshmanrekha.protect.detection.ScamDetector
import com.lakshmanrekha.protect.utils.RuntimeState

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        // 🚫 DO NOT ANALYZE OUR OWN NOTIFICATIONS
        if (sbn.packageName == packageName) return

        // 🚫 Global bypass (SOS / overlays / emergency)
        if (RuntimeState.bypassProtection) return

        val extras = sbn.notification.extras ?: return

        val title = extras.getCharSequence("android.title")?.toString()
        val text = extras.getCharSequence("android.text")?.toString()

        val detectedText = listOfNotNull(title, text)
            .joinToString(" ")
            .trim()

        // 🚫 Ignore empty / junk notifications
        if (detectedText.length < 10) return

        val sourceApp = sbn.packageName
        RuntimeState.activeSourceApp = sourceApp

        /* -------------------------------------------------
         * RAPID APP SWITCHING DETECTION
         * ------------------------------------------------- */
        val now = System.currentTimeMillis()
        RuntimeState.lastForegroundApp?.let { last ->
            if (last != sourceApp && now - RuntimeState.lastAppSwitchTime < 2000) {
                RuntimeState.rapidAppSwitching = true
            }
        }
        RuntimeState.lastForegroundApp = sourceApp
        RuntimeState.lastAppSwitchTime = now

        /* -------------------------------------------------
         * UPI / BANK APP DURING CALL
         * ------------------------------------------------- */
        if (RuntimeState.callOngoing) {
            if (
                sourceApp.contains("upi", true) ||
                sourceApp.contains("pay", true) ||
                sourceApp.contains("bank", true)
            ) {
                RuntimeState.upiOpenedDuringCall = true
            }
        }

        /* -------------------------------------------------
         * OTP HEURISTIC
         * ------------------------------------------------- */
        if (
            RuntimeState.callOngoing &&
            detectedText.contains("otp", ignoreCase = true)
        ) {
            RuntimeState.otpPatternDetected = true
        }

        evaluateThreat(detectedText)
    }

    private fun evaluateThreat(text: String) {

        val model = RuntimeState.scamRiskModel ?: return

        val threat = try {
            ScamDetector.analyzeSituation(
                detectedText = text,
                mlModel = model,
                callOngoing = RuntimeState.callOngoing,
                callerTrusted = RuntimeState.currentCallerTrusted,
                upiOpenedDuringCall = RuntimeState.upiOpenedDuringCall,
                rapidAppSwitching = RuntimeState.rapidAppSwitching,
                otpPatternDetected = RuntimeState.otpPatternDetected
            )
        } catch (e: Exception) {
            // 🚫 NEVER crash notification thread
            return
        }

        if (threat.score > 0) {
            ProtectionManager.handleThreat(applicationContext, threat)
        }
    }
}