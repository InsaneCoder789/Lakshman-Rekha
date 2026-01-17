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

        ThreatLogger.logSystem("Raksha mode handling ${threat.level}")

        if (threat.level != ThreatLevel.DANGEROUS) return
        if (RuntimeState.emergencyTriggered) return
        if (!RuntimeState.appInForeground) return

        RuntimeState.emergencyTriggered = true

        OverlayService.showEmergency(context)
        EmergencyAlertService.trigger(context)
    }
}