package com.lakshmanrekha.protect.modes

import android.content.Context
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.services.EmergencyAlertService
import com.lakshmanrekha.protect.services.OverlayService
import com.lakshmanrekha.protect.utils.RuntimeState
import com.lakshmanrekha.protect.utils.ThreatLogger

object RakshaActions {

    fun apply(context: Context, threat: Threat) {

        ThreatLogger.logSystem(
            "Raksha mode handling ${threat.level}"
        )

        // 🚨 ONLY for DANGEROUS
        if (threat.level != ThreatLevel.DANGEROUS) return

        // Prevent duplicate emergencies
        if (RuntimeState.emergencyTriggered) return

        RuntimeState.emergencyTriggered = true

        // 1️⃣ Emergency overlay
        OverlayService.showEmergency(context)

        // 2️⃣ Trigger SOS flow
        EmergencyAlertService.trigger(context)
    }
}