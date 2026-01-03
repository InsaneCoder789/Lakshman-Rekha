package com.lakshmanrekha.protect.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lakshmanrekha.protect.core.ProtectionManager
import com.lakshmanrekha.protect.detection.ScamDetector
import com.lakshmanrekha.protect.utils.RuntimeState

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName

        // Detect risky app openings
        if (RuntimeState.callOngoing) {
            if (packageName.contains("upi", true) ||
                packageName.contains("pay", true)
            ) {
                RuntimeState.upiOpenedDuringCall = true
            }
        }

        evaluateThreat()
    }

    private fun evaluateThreat() {

        val threat = ScamDetector.analyzeSituation(
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