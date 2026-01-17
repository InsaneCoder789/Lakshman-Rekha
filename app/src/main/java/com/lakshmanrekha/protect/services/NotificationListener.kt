package com.lakshmanrekha.protect.services

import android.Manifest
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresPermission
import com.lakshmanrekha.protect.core.ProtectionManager
import com.lakshmanrekha.protect.detection.ScamDetector
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.RuntimeState

class NotificationListener : NotificationListenerService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        if (!AppState.isSetupComplete) return
        if (RuntimeState.bypassProtection) return
        if (sbn == null) return
        if (sbn.packageName == packageName) return

        val extras = sbn.notification.extras ?: return

        val title = extras.getCharSequence("android.title")?.toString()
        val text = extras.getCharSequence("android.text")?.toString()

        val detectedText = listOfNotNull(title, text)
            .joinToString(" ")
            .trim()

        if (detectedText.length < 10) return

        val sourceApp = sbn.packageName
        RuntimeState.activeSourceApp = sourceApp

        val now = System.currentTimeMillis()
        RuntimeState.lastForegroundApp?.let { last ->
            if (last != sourceApp && now - RuntimeState.lastAppSwitchTime < 2000) {
                RuntimeState.rapidAppSwitching = true
            }
        }
        RuntimeState.lastForegroundApp = sourceApp
        RuntimeState.lastAppSwitchTime = now

        if (RuntimeState.callOngoing &&
            (sourceApp.contains("upi", true) ||
                    sourceApp.contains("pay", true) ||
                    sourceApp.contains("bank", true))
        ) {
            RuntimeState.upiOpenedDuringCall = true
        }

        if (RuntimeState.callOngoing &&
            detectedText.contains("otp", ignoreCase = true)
        ) {
            RuntimeState.otpPatternDetected = true
        }

        evaluateThreat(detectedText)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
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
        } catch (_: Exception) {
            return
        }

        if (threat.score > 0) {
            ProtectionManager.handleThreat(applicationContext, threat)
        }
    }
}