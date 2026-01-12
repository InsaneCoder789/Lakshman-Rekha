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

            // Primary scam classification
            if (signals.isScam) {
                score += 20
                reasons.add("Message matches known scam patterns")
            }

            // Severity (dataset aligned: 1–5)
            score += signals.severity * 8
            reasons.add("Scam severity level: ${signals.severity}")

            // Scam progression stage
            when (signals.scamStage) {
                ScamStage.LURE -> {
                    score += 5
                    reasons.add("Initial scam lure detected")
                }
                ScamStage.ACTION -> {
                    score += 15
                    reasons.add("User is being asked to take action")
                }
                ScamStage.THREAT -> {
                    score += 30
                    reasons.add("Threat or coercion stage detected")
                }
            }

            // Requested action intent
            signals.requestedAction?.let { action ->
                when (action) {

                    ScamAction.PAY_UPI,
                    ScamAction.SEND_OTP -> {
                        score += 20
                        reasons.add("High-risk action requested: ${action.name}")
                    }

                    ScamAction.INSTALL_APP -> {
                        score += 25
                        reasons.add("Remote access app installation requested")
                    }

                    ScamAction.CALL_NUMBER,
                    ScamAction.CLICK_LINK -> {
                        score += 10
                        reasons.add("Suspicious redirection requested")
                    }

                    ScamAction.SHARE_DETAILS -> {
                        score += 15
                        reasons.add("Sensitive personal details requested")
                    }

                    ScamAction.UNKNOWN -> {
                        // Intentionally no score impact
                    }
                }
            }

            // Binary risk indicators
            if (signals.hasOtp) {
                score += 15
                reasons.add("OTP request detected")
            }

            if (signals.hasUpi) {
                score += 15
                reasons.add("Payment request detected")
            }

            if (signals.hasUrl) {
                score += 10
                reasons.add("Suspicious link detected")
            }

            if (signals.hasThreat) {
                score += 20
                reasons.add("Threatening language detected")
            }

            if (signals.hasUrgency) {
                score += 10
                reasons.add("Urgency pressure detected")
            }
        }

        /* -------------------------------------------------
         * NORMALIZE SCORE
         * ------------------------------------------------- */

        score = score.coerceIn(0, 100)

        /* -------------------------------------------------
         * MAP SCORE → THREAT LEVEL
         * ------------------------------------------------- */

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