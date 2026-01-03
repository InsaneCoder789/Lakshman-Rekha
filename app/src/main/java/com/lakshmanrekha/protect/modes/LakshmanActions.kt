package com.lakshmanrekha.protect.modes

import android.content.Context
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.services.OverlayService
import com.lakshmanrekha.protect.utils.ThreatLogger

object LakshmanActions {

    /**
     * Lakshman mode:
     * - Warn the user
     * - Educate (coach screen triggered by ProtectionManager)
     * - Do NOT panic
     */
    fun apply(context: Context, threat: Threat) {

        // Log action
        ThreatLogger.logSystem(
            "Lakshman mode applied for threat level: ${threat.level}"
        )

        // Only warn for risky or higher
        if (
            threat.level == ThreatLevel.CAUTION ||
            threat.level == ThreatLevel.RISKY ||
            threat.level == ThreatLevel.DANGEROUS
        ) {
            OverlayService.showWarning(context)
        }
    }
}