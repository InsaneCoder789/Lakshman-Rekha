package com.lakshmanrekha.protect.core

import android.Manifest
import android.content.Context
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun handleThreat(context: Context, threat: Threat) {

        if (isTrustedSource(threat)) {
            ThreatLogger.logSystem("Trusted source bypassed")
            return
        }

        // 🧭 Always apply Saathi baseline
        SaathiActions.apply(context, threat)

        // 📌 Store post-call summary
        RuntimeState.lastThreatLevel = threat.level
        RuntimeState.lastThreatReasons = threat.reasons

        if (threat.level != ThreatLevel.SAFE) {
            RuntimeState.postCallSummaryPending = true
        }

        val effectiveMode = getEffectiveMode(context)

        // 🧠 AUTO-COACH TRIGGER (KEY PART)
        maybeTriggerCoach(context, threat, effectiveMode)

        // 🛡️ Mode escalation
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

    private fun maybeTriggerCoach(
        context: Context,
        threat: Threat,
        mode: ProtectionMode
    ) {

        // ❌ Never coach on SAFE or DANGEROUS
        if (
            threat.level == ThreatLevel.SAFE ||
            threat.level == ThreatLevel.DANGEROUS
        ) return

        // ❌ Do not repeat
        if (RuntimeState.coachShown) return

        // ❌ Saathi is guidance-only (no interruptions)
        if (mode == ProtectionMode.SAATHI) return

        // ✅ Only CAUTION / RISKY reach here
        RuntimeState.coachShown = true

        ThreatLogger.logSystem(
            "📘 Coach triggered for threat: ${threat.level}"
        )

        CoachLauncher.launch(context)
    }
}