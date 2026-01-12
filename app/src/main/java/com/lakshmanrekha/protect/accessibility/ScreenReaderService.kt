package com.lakshmanrekha.protect.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.lakshmanrekha.protect.core.ProtectionManager
import com.lakshmanrekha.protect.detection.ScamDetector
import com.lakshmanrekha.protect.utils.RuntimeState
import com.lakshmanrekha.protect.utils.ThreatLogger

class ScreenReaderService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val text = event.text
            .joinToString(" ") { it.toString() }
            .trim()

        if (text.isBlank()) return

        ThreatLogger.logSystem("🖥️ Screen text detected")

        evaluateScreenText(text)
    }

    override fun onInterrupt() {
        ThreatLogger.logSystem("ScreenReader interrupted")
    }

    private fun evaluateScreenText(text: String) {

        // Avoid noisy triggers
        if (!RuntimeState.callOngoing && RuntimeState.activeSourceApp == null) return

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
            try {
                ProtectionManager.handleThreat(applicationContext, threat)
            } catch (e: SecurityException) {
                // Accessibility service must never crash
                ThreatLogger.logSystem("Permission blocked during screen threat handling")
            }
        }
    }
}