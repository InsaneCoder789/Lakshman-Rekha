package com.lakshmanrekha.protect.core

import android.content.Context
import android.provider.Settings
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.modes.*
import com.lakshmanrekha.protect.utils.*

object ProtectionManager {

    fun getEffectiveMode(context: Context): ProtectionMode {
        val requested = AppState.protectionMode

        if (requested == ProtectionMode.RAKSHA && !supportsRaksha(context)) {
            ThreatLogger.logSystem(
                "Raksha unsupported. Downgraded to Lakshman."
            )

            AppState.lastDowngradeReason =
                "This phone does not support full Raksha protection."

            AppState.protectionMode = ProtectionMode.LAKSHMAN
            AppPrefs.updateMode(context, ProtectionMode.LAKSHMAN)

            return ProtectionMode.LAKSHMAN
        }

        return requested
    }

    fun handleThreat(context: Context, threat: Threat) {

        if (isTrustedSource(threat)) {
            ThreatLogger.logSystem("Trusted source bypassed")
            return
        }

        // Saathi is ALWAYS applied
        SaathiActions.apply(context, threat)

        RuntimeState.lastThreatLevel = threat.level
        RuntimeState.lastThreatReasons = threat.reasons

        when (getEffectiveMode(context)) {
            ProtectionMode.SAATHI -> Unit
            ProtectionMode.LAKSHMAN -> LakshmanActions.apply(context, threat)
            ProtectionMode.RAKSHA -> {
                LakshmanActions.apply(context, threat)
                RakshaActions.apply(context, threat)
            }
            ProtectionMode.NONE -> Unit
        }
    }

    private fun supportsRaksha(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    private fun isTrustedSource(threat: Threat): Boolean {
        threat.sourceNumber?.let {
            if (AppState.trustedContacts.contains(it)) return true
        }
        threat.sourceApp?.let {
            if (AppState.trustedApps.contains(it)) return true
        }
        return false
    }
}