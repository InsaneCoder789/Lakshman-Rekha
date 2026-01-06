package com.lakshmanrekha.protect.detection

import com.lakshmanrekha.protect.ml.ScamLabel
import com.lakshmanrekha.protect.ml.ScamRiskModel
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
        return analyzeSituation(
            detectedText = null,
            mlModel = null,
            callOngoing = callOngoing,
            callerTrusted = callerTrusted,
            upiOpenedDuringCall = upiOpenedDuringCall,
            rapidAppSwitching = rapidAppSwitching,
            otpPatternDetected = otpPatternDetected
        )
    }

    fun analyzeSituation(
        detectedText: String?,
        mlModel: ScamRiskModel?,
        callOngoing: Boolean,
        callerTrusted: Boolean,
        upiOpenedDuringCall: Boolean,
        rapidAppSwitching: Boolean,
        otpPatternDetected: Boolean
    ): Threat {

        var score = 0
        val reasons = mutableListOf<String>()

        // ---------- RULES ----------
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
            reasons.add("Urgent app switching detected")
        }

        if (callerTrusted) {
            score -= 50
            reasons.add("Caller is a trusted contact")
        }

        // ---------- ML ----------
        if (!detectedText.isNullOrBlank() && mlModel != null) {
            val prediction = mlModel.predict(detectedText)

            when (prediction.label) {
                ScamLabel.LIKELY_SCAM -> {
                    score += 40
                    reasons.add("Message resembles scam patterns")
                }
                ScamLabel.SUSPICIOUS -> {
                    score += 20
                    reasons.add("Suspicious wording detected")
                }
                ScamLabel.SAFE -> Unit
            }
        }

        score = score.coerceIn(0, 100)

        val level = when {
            score <= 20 -> ThreatLevel.SAFE
            score <= 50 -> ThreatLevel.CAUTION
            score <= 75 -> ThreatLevel.RISKY
            else -> ThreatLevel.DANGEROUS
        }

        return Threat(level, score, reasons.distinct())
    }
}