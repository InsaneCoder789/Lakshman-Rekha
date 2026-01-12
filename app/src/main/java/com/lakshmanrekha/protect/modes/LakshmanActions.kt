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

        // ❌ Ignore SAFE
        if (threat.level < ThreatLevel.CAUTION) return

        // ❌ Do not interfere during post-call summary
        if (RuntimeState.postCallSummaryPending) return

        // 1️⃣ Warning overlay (ONCE)
        if (!RuntimeState.overlayShown) {
            OverlayService.showWarning(context)
            RuntimeState.overlayShown = true
        }

        // 2️⃣ Coach only when risk is meaningful
        val shouldCoach =
            threat.level >= ThreatLevel.RISKY &&
                    (RuntimeState.otpPatternDetected || RuntimeState.upiOpenedDuringCall)

        if (shouldCoach && !RuntimeState.warningShown) {
            CoachLauncher.launch(context)
            RuntimeState.warningShown = true
        }
    }
}