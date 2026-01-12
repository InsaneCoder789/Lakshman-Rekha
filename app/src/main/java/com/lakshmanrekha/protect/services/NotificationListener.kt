package com.lakshmanrekha.protect.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lakshmanrekha.protect.core.ProtectionManager
import com.lakshmanrekha.protect.detection.ScamDetector
import com.lakshmanrekha.protect.utils.RuntimeState

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || RuntimeState.bypassProtection) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString()
        val text = extras.getCharSequence("android.text")?.toString()

        val detectedText = listOfNotNull(title, text)
            .joinToString(" ")
            .trim()
            .takeIf { it.isNotEmpty() }

        val packageName = sbn.packageName
        RuntimeState.activeSourceApp = packageName

        // 🔁 Rapid app switching
        val now = System.currentTimeMillis()
        RuntimeState.lastForegroundApp?.let { last ->
            if (last != packageName && now - RuntimeState.lastAppSwitchTime < 2000) {
                RuntimeState.rapidAppSwitching = true
            }
        }
        RuntimeState.lastForegroundApp = packageName
        RuntimeState.lastAppSwitchTime = now

        if (RuntimeState.callOngoing) {
            if (
                packageName.contains("upi", true) ||
                packageName.contains("pay", true) ||
                packageName.contains("bank", true)
            ) {
                RuntimeState.upiOpenedDuringCall = true
            }
        }

        if (
            detectedText != null &&
            RuntimeState.callOngoing &&
            detectedText.contains("otp", true)
        ) {
            RuntimeState.otpPatternDetected = true
        }

        evaluateThreat(detectedText)
    }

    private fun evaluateThreat(text: String?) {

        val threat = ScamDetector.analyzeSituation(
            detectedText = text,
            mlModel = RuntimeState.scamRiskModel,
            callOngoing = RuntimeState.callOngoing,
            callerTrusted = RuntimeState.currentCallerTrusted,
            upiOpenedDuringCall = RuntimeState.upiOpenedDuringCall,
            rapidAppSwitching = RuntimeState.rapidAppSwitching,
            otpPatternDetected = RuntimeState.otpPatternDetected
        )

        if (threat.score > 0) {
            ProtectionManager.handleThreat(applicationContext, threat)
        }
    }
}