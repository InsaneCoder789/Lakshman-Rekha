package com.lakshmanrekha.protect.ml

/**
 * ML output = signals, NOT decisions
 * Decisions are taken by ScamDetector
 */
data class ScamSignals(

    // Primary classification
    val isScam: Boolean,
    val severity: Int,            // 1–5

    // Scam understanding
    val scamType: String?,        // optional / future
    val scamStage: ScamStage,
    val requestedAction: ScamAction,

    // Binary risk signals
    val hasOtp: Boolean,
    val hasUpi: Boolean,
    val hasUrl: Boolean,
    val hasThreat: Boolean,
    val hasUrgency: Boolean,

    // Meta
    val confidence: Float,
    val explanation: String
)