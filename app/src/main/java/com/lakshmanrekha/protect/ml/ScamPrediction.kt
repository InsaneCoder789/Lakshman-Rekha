package com.lakshmanrekha.protect.ml

enum class ScamLabel {
    SAFE,
    SUSPICIOUS,
    LIKELY_SCAM
}

data class ScamPrediction(
    val label: ScamLabel,
    val confidence: Float,
    val explanation: String
)