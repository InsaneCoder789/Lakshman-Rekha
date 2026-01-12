package com.lakshmanrekha.protect.reporting

data class ScamReport(
    val threatLevel: String,
    val reasons: List<String>,
    val language: String,
    val sourceType: String,
    val timestamp: Long
)