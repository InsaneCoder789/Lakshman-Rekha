package com.lakshmanrekha.protect.model

enum class ThreatLevel {
    SAFE,
    CAUTION,
    RISKY,
    DANGEROUS
}

data class Threat(
    val level: ThreatLevel,
    val score: Int,
    val reasons: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)