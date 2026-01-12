package com.lakshmanrekha.protect.reporting

import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.utils.*

object CitizenReportManager {

    private val reportedHashes = mutableSetOf<Int>()

    fun report(threat: Threat) {

        val hash = generateHash(threat)
        if (reportedHashes.contains(hash)) {
            ThreatLogger.logSystem("Duplicate scam report ignored")
            return
        }

        reportedHashes.add(hash)

        val report = ScamReport(
            threatLevel = threat.level.name,
            reasons = threat.reasons,
            language = AppState.language?.name ?: "EN",
            sourceType = threat.sourceApp ?: "UNKNOWN",
            timestamp = threat.timestamp
        )

        // 🔒 MOCK UPLOAD
        ThreatLogger.logSystem("📡 Scam report queued (mock): $report")
    }

    private fun generateHash(threat: Threat): Int {
        return (threat.level.name + threat.reasons.joinToString()).hashCode()
    }
}