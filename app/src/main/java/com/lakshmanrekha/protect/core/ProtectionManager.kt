package com.lakshmanrekha.protect.core

import android.content.Context
import android.provider.Settings
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.modes.LakshmanActions
import com.lakshmanrekha.protect.modes.RakshaActions
import com.lakshmanrekha.protect.modes.SaathiActions
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ProtectionMode
import com.lakshmanrekha.protect.utils.RuntimeState
import com.lakshmanrekha.protect.utils.ThreatLogger

object ProtectionManager {

    fun getEffectiveMode(context: Context): ProtectionMode {
        val requested = AppState.protectionMode

        if (requested == ProtectionMode.RAKSHA && !supportsRaksha(context)) {

            ThreatLogger.logSystem(
                "Raksha unsupported on this device. Downgraded to Lakshman."
            )

            AppState.lastDowngradeReason =
                "This phone does not support full Raksha protection."

            AppState.protectionMode = ProtectionMode.LAKSHMAN
            return ProtectionMode.LAKSHMAN
        }

        return requested
    }

    /**
     * MAIN ENTRY POINT
     * Called by NotificationListener / Call detectors
     */
    fun handleThreat(context: Context, threat: Threat) {

        // 📌 Always log (base layer – Saathi)
        SaathiActions.apply(context, threat)

        // 📌 Save for post-call summary
        RuntimeState.lastCallThreatLevel = threat.level
        RuntimeState.lastCallReasons = threat.reasons

        val effectiveMode = getEffectiveMode(context)

        when (effectiveMode) {

            ProtectionMode.SAATHI -> {
            }

            ProtectionMode.LAKSHMAN -> {
                LakshmanActions.apply(context, threat)
            }

            ProtectionMode.RAKSHA -> {
                LakshmanActions.apply(context, threat)
                RakshaActions.apply(context, threat)
            }

            ProtectionMode.NONE -> {
            }
        }
    }
    private fun supportsRaksha(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}