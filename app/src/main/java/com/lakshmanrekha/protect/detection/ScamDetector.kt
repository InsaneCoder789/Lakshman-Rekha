package com.lakshmanrekha.protect.detection

import com.lakshmanrekha.protect.ml.ScamAction
import com.lakshmanrekha.protect.ml.ScamRiskModel
import com.lakshmanrekha.protect.ml.ScamSignals
import com.lakshmanrekha.protect.ml.ScamStage
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.RuntimeState

/**
 * ScamDetector
 *
 * Responsibility:
 * - Combine ML signals + runtime context
 * - Compute a deterministic risk score
 * - Produce human-readable reasons
 *
 * ❗ ML DOES NOT DECIDE ACTIONS
 * ❗ ML ONLY PROVIDES SIGNALS
 */
object ScamDetector {

    private fun applyMlScore(baseScore: Int, confidence: Float): Int {
        val multiplier = when {
            confidence >= 0.85f -> 1.0f
            confidence >= 0.7f -> 0.85f
            confidence >= 0.55f -> 0.7f
            else -> 0.45f
        }
        return (baseScore * multiplier).toInt()
    }

    /* =====================================================
     * PUBLIC ENTRY (RULES ONLY – BACKWARD SAFE)
     * ===================================================== */

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

    /* =====================================================
     * MAIN ENTRY (ML + RULES)
     * ===================================================== */

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

        /* -------------------------------------------------
         * RULE-BASED CONTEXT (REAL-TIME BEHAVIOR)
         * ------------------------------------------------- */

        if (callOngoing && !callerTrusted) {
            score += 20
            reasons.add("Unknown caller during active call")
        }

        if (upiOpenedDuringCall && !callerTrusted) {
            score += 25
            reasons.add("Payment app opened during call")
        }

        if (otpPatternDetected && callOngoing && !callerTrusted) {
            score += 30
            reasons.add("OTP-related activity during call")
        }

        if (rapidAppSwitching) {
            score += 15
            reasons.add("Rapid app switching detected")
        }

        if (callerTrusted) {
            score -= 40
            reasons.add("Caller is a trusted contact")
        }

        /* -------------------------------------------------
         * ML-BASED SIGNALS (TEXT / NOTIFICATION / SCREEN)
         * ------------------------------------------------- */

        if (!detectedText.isNullOrBlank() && mlModel != null) {

            val signals: ScamSignals = mlModel.predict(detectedText)
            if (signals.usedFallback) {
                reasons.add("ML unavailable, using rule-based protection")
            } else {
                var mlScore = 0

                if (signals.isScam) {
                    mlScore += 18
                    reasons.add("Message matches known scam patterns")
                }

                mlScore += signals.severity * 6
                reasons.add("Scam severity level: ${signals.severity}")

                when (signals.scamStage) {
                    ScamStage.LURE -> {
                        mlScore += 4
                        reasons.add("Initial scam lure detected")
                    }
                    ScamStage.ACTION -> {
                        mlScore += 12
                        reasons.add("User is being asked to take action")
                    }
                    ScamStage.THREAT -> {
                        mlScore += 22
                        reasons.add("Threat or coercion stage detected")
                    }
                }

                when (signals.requestedAction) {
                    ScamAction.PAY_UPI,
                    ScamAction.SEND_OTP -> {
                        mlScore += 18
                        reasons.add("High-risk action requested: ${signals.requestedAction.name}")
                    }

                    ScamAction.INSTALL_APP -> {
                        mlScore += 22
                        reasons.add("Remote access app installation requested")
                    }

                    ScamAction.CALL_NUMBER,
                    ScamAction.CLICK_LINK -> {
                        mlScore += 9
                        reasons.add("Suspicious redirection requested")
                    }

                    ScamAction.SHARE_DETAILS -> {
                        mlScore += 14
                        reasons.add("Sensitive personal details requested")
                    }

                    ScamAction.VISIT_OFFICIAL -> {
                        reasons.add("Official verification path mentioned")
                    }

                    ScamAction.NO_ACTION,
                    ScamAction.UNKNOWN -> Unit
                }

                if (signals.scamType != null && signals.isScam) {
                    reasons.add("Detected pattern family: ${signals.scamType}")
                }

                var indicatorScore = 0
                if (signals.hasOtp) {
                    indicatorScore += 12
                    reasons.add("OTP request detected")
                }

                if (signals.hasUpi) {
                    indicatorScore += 12
                    reasons.add("Payment request detected")
                }

                if (signals.hasUrl) {
                    indicatorScore += 8
                    reasons.add("Suspicious link detected")
                }

                if (signals.hasQr) {
                    indicatorScore += 10
                    reasons.add("QR-based payment prompt detected")
                }

                if (signals.hasPhone) {
                    indicatorScore += 6
                    reasons.add("Suspicious callback number detected")
                }

                if (signals.hasThreat) {
                    indicatorScore += 16
                    reasons.add("Threatening language detected")
                }

                if (signals.hasUrgency) {
                    indicatorScore += 8
                    reasons.add("Urgency pressure detected")
                }

                indicatorScore = indicatorScore.coerceAtMost(30)
                score += applyMlScore(mlScore + indicatorScore, signals.confidence)
                if (signals.confidence < 0.6f) {
                    reasons.add("ML confidence was low, impact reduced")
                } else {
                    reasons.add("ML confidence: ${(signals.confidence * 100).toInt()}%")
                }
            }
        }

        /* -------------------------------------------------
         * TRUSTED SOURCE CORRECTION
         * ------------------------------------------------- */

        if (callerTrusted) {
            score = minOf(score, 25)
        }

        /* -------------------------------------------------
         * SCORE SAFETY NETS
         * ------------------------------------------------- */

        if (detectedText.isNullOrBlank() && !callOngoing && !otpPatternDetected && !upiOpenedDuringCall) {
            score = 0
            reasons.clear()
            reasons.add("No risk signals found")
        }

        if (score < 0) {
            score = 0
        }

        if (score == 0 && reasons.isEmpty()) {
            reasons.add("No strong threat indicators detected")
        }

        score = score.coerceIn(0, 100)

        val level = when {
            score <= 20 -> ThreatLevel.SAFE
            score <= 45 -> ThreatLevel.CAUTION
            score <= 70 -> ThreatLevel.RISKY
            else -> ThreatLevel.DANGEROUS
        }

        return Threat(
            level = level,
            score = score,
            reasons = reasons.distinct(),
            sourceNumber = RuntimeState.activeSourceNumber,
            sourceApp = RuntimeState.activeSourceApp
        )
    }
}
