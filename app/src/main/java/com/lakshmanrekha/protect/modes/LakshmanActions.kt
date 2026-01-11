package com.lakshmanrekha.protect.modes

import android.content.Context
import com.lakshmanrekha.protect.core.CoachLauncher
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.services.OverlayService
import com.lakshmanrekha.protect.utils.RuntimeState
import com.lakshmanrekha.protect.utils.ThreatLogger

object LakshmanActions {

    fun apply(context: Context, threat: Threat) {

        ThreatLogger.logSystem(
            "Lakshman mode handling ${threat.level}"
        )

        // Only intervene for CAUTION+
        if (threat.level < ThreatLevel.CAUTION) return

        // 1️⃣ Show warning overlay ONCE
        if (!RuntimeState.overlayShown) {
            OverlayService.showWarning(context)
            RuntimeState.overlayShown = true
        }

        // 2️⃣ Auto-trigger coach for strong signals
        val shouldCoach =
            threat.level >= ThreatLevel.RISKY ||
                    RuntimeState.otpPatternDetected ||
                    RuntimeState.upiOpenedDuringCall

        if (shouldCoach && !RuntimeState.warningShown) {
            CoachLauncher.launch(context)
            RuntimeState.warningShown = true
        }
    }
}