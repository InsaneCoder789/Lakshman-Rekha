package com.lakshmanrekha.protect.ml

data class ScamSignals(
    val isScam: Boolean,
    val severity: Int,
    val scamType: String?,
    val scamStage: ScamStage,
    val requestedAction: ScamAction,
    val hasOtp: Boolean,
    val hasUpi: Boolean,
    val hasUrl: Boolean,
    val hasQr: Boolean,
    val hasPhone: Boolean,
    val hasThreat: Boolean,
    val hasUrgency: Boolean,
    val confidence: Float,
    val explanation: String,
    val usedFallback: Boolean,
    val fallbackReason: String?
) {
    companion object {
        fun safeFallback(reason: String) = ScamSignals(
            isScam = false,
            severity = 1,
            scamType = null,
            scamStage = ScamStage.LURE,
            requestedAction = ScamAction.UNKNOWN,
            hasOtp = false,
            hasUpi = false,
            hasUrl = false,
            hasQr = false,
            hasPhone = false,
            hasThreat = false,
            hasUrgency = false,
            confidence = 0f,
            explanation = reason,
            usedFallback = true,
            fallbackReason = reason
        )
    }
}
