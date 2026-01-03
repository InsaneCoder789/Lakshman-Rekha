package com.lakshmanrekha.protect.detection

import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel

object ScamDetector {

    fun analyzeSituation(
        callOngoing: Boolean,
        callerTrusted: Boolean,
        upiOpenedDuringCall: Boolean,
        rapidAppSwitching: Boolean,
        otpPatternDetected: Boolean
    ): Threat {

        var score = 0
        val reasons = mutableListOf<String>()

        if (callOngoing && !callerTrusted) {
            score += 30
            reasons.add("Unknown call in progress")
        }

        if (upiOpenedDuringCall && !callerTrusted) {
            score += 30
            reasons.add("Payment app opened during call")
        }

        if (otpPatternDetected && callOngoing && !callerTrusted) {
            score += 40
            reasons.add("OTP activity during unknown call")
        }

        if (rapidAppSwitching) {
            score += 15
            reasons.add("Urgent app switching pattern")
        }

        if (callerTrusted) {
            score -= 50
            reasons.add("Trusted family contact")
        }

        val level = when {
            score <= 20 -> ThreatLevel.SAFE
            score <= 50 -> ThreatLevel.CAUTION
            score <= 75 -> ThreatLevel.RISKY
            else -> ThreatLevel.DANGEROUS
        }

        return Threat(level, score.coerceIn(0, 100), reasons)
    }
}