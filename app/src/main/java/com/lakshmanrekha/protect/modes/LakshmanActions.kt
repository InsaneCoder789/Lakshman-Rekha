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

        // Reset shown flags if the threat clears to SAFE
        if (threat.level == ThreatLevel.SAFE) {
            RuntimeState.warningShown = false
            RuntimeState.overlayShown = false
            return
        }

        // Ignore if level is CAUTION or less (Lakshman only triggers on high risk)
        if (threat.level.ordinal < ThreatLevel.RISKY.ordinal) return

        // Do not interfere during post-call summary
        if (RuntimeState.postCallSummaryPending) return

        // 1️⃣ Warning overlay
        if (!RuntimeState.overlayShown) {
            OverlayService.showWarning(context)
            RuntimeState.overlayShown = true
        }

        // 2️⃣ Coach only when risk is meaningful
        // Triggers if risky+ and payment/OTP related activity is detected
        val shouldCoach =
            threat.level >= ThreatLevel.RISKY &&
                    (RuntimeState.otpPatternDetected || RuntimeState.upiOpenedDuringCall)

        if (shouldCoach && !RuntimeState.warningShown) {
            CoachLauncher.launch(context)
            RuntimeState.warningShown = true
        }
    }
}