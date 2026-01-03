package com.lakshmanrekha.protect.modes

import android.content.Context
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.services.EmergencyAlertService
import com.lakshmanrekha.protect.services.OverlayService
import com.lakshmanrekha.protect.utils.ThreatLogger

object RakshaActions {
    fun apply(context: Context, threat: Threat) {

        // Always log
        ThreatLogger.logSystem(
            "Raksha mode applied for threat level: ${threat.level}"
        )

        // Only escalate when truly dangerous
        if (threat.level == ThreatLevel.DANGEROUS) {
            OverlayService.showEmergency(context)
            EmergencyAlertService.trigger(context)
        }
    }
}