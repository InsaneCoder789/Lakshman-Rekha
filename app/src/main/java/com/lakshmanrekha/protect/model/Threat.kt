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
    val sourceNumber: String? = null,   // Phone number (calls/SMS)
    val sourceApp: String? = null,      // App/package name (WhatsApp, browser, etc.)
    val timestamp: Long = System.currentTimeMillis()
)