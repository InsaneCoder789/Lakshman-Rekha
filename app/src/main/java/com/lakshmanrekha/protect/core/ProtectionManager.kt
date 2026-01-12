package com.lakshmanrekha.protect.core

import android.Manifest
import android.content.Context
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.modes.LakshmanActions
import com.lakshmanrekha.protect.modes.RakshaActions
import com.lakshmanrekha.protect.modes.SaathiActions
import com.lakshmanrekha.protect.services.FamilyAlertService
import com.lakshmanrekha.protect.utils.*

object ProtectionManager {

    /* =========================================================
     * MODE RESOLUTION
     * ========================================================= */

    fun getEffectiveMode(context: Context): ProtectionMode {
        val requested = AppState.protectionMode

        if (requested == ProtectionMode.RAKSHA && !supportsRaksha(context)) {

            ThreatLogger.logSystem(
                "Raksha unsupported. Downgrading to Lakshman."
            )

            AppState.lastDowngradeReason =
                "This device does not support full Raksha protection."

            AppState.protectionMode = ProtectionMode.LAKSHMAN
            AppPrefs.updateMode(context, ProtectionMode.LAKSHMAN)

            return ProtectionMode.LAKSHMAN
        }

        return requested
    }

    /* =========================================================
     * MAIN ENTRY — ALL THREATS FLOW THROUGH HERE
     * ========================================================= */

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun handleThreat(context: Context, threat: Threat) {

        // ❌ Trusted sources are bypassed
        if (isTrustedSource(threat)) {
            ThreatLogger.logSystem("Trusted source bypassed")
            return
        }

        // 🧭 1️⃣ Saathi baseline (always)
        SaathiActions.apply(context, threat)

        // 📌 2️⃣ Save post-call summary data
        RuntimeState.lastThreatLevel = threat.level
        RuntimeState.lastThreatReasons = threat.reasons

        if (threat.level != ThreatLevel.SAFE) {
            RuntimeState.postCallSummaryPending = true
        }

        // 👨‍👩‍👧 3️⃣ FAMILY ALERT (NON-SOS, OPT-IN)
        if (
            threat.level == ThreatLevel.DANGEROUS &&
            AppState.familyAlertsEnabled &&
            !RuntimeState.emergencyTriggered
        ) {
            FamilyAlertService.notifyTrustedContacts(context, threat)
        }

        val effectiveMode = getEffectiveMode(context)

        // 🛡️ 4️⃣ Mode escalation
        when (effectiveMode) {

            ProtectionMode.SAATHI -> Unit

            ProtectionMode.LAKSHMAN -> {
                LakshmanActions.apply(context, threat)
            }

            ProtectionMode.RAKSHA -> {
                LakshmanActions.apply(context, threat)
                RakshaActions.apply(context, threat)
            }

            ProtectionMode.NONE -> Unit
        }
    }

    /* =========================================================
     * HELPERS
     * ========================================================= */

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