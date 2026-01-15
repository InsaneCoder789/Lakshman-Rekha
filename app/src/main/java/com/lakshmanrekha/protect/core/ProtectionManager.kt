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

        // Lakshman is now the strongest mode → requires overlay permission
        if (requested == ProtectionMode.LAKSHMAN && !supportsLakshman(context)) {

            ThreatLogger.logSystem(
                "Lakshman unsupported. Downgrading to Raksha."
            )

            AppState.lastDowngradeReason =
                "This device does not support full Lakshman protection."

            AppState.protectionMode = ProtectionMode.RAKSHA
            AppPrefs.updateMode(context, ProtectionMode.RAKSHA)

            return ProtectionMode.RAKSHA
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

        RuntimeState.lastThreatLevel = threat.level
        RuntimeState.lastThreatReasons = threat.reasons

        if (threat.level != ThreatLevel.SAFE) {
            RuntimeState.postCallSummaryPending = true
        }

        // 👨‍👩‍👧 2️⃣ Family alert (dangerous only, opt-in)
        if (
            threat.level == ThreatLevel.DANGEROUS &&
            AppState.familyAlertsEnabled &&
            !RuntimeState.emergencyTriggered
        ) {
            FamilyAlertService.notifyTrustedContacts(context, threat)
        }

        val effectiveMode = getEffectiveMode(context)

        // 🛡️ 3️⃣ Escalation ladder (UPDATED SEMANTICS)
        when (effectiveMode) {

            ProtectionMode.SAATHI -> Unit

            // 🟡 Guided protection
            ProtectionMode.RAKSHA -> {
                LakshmanActions.apply(context, threat)
            }

            // 🔴 Autonomous protection
            ProtectionMode.LAKSHMAN -> {
                LakshmanActions.apply(context, threat)
                RakshaActions.apply(context, threat)
            }

            ProtectionMode.NONE -> Unit
        }
    }

    /* =========================================================
     * HELPERS
     * ========================================================= */

    private fun supportsLakshman(context: Context): Boolean =
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