package com.lakshmanrekha.protect.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * SINGLE SOURCE OF TRUTH for app-wide runtime state.
 * This object MUST stay lightweight and deterministic.
 */
object AppState {

    /* -------------------------------------------------
     * USER PROFILE
     * ------------------------------------------------- */
    var name by mutableStateOf("")
    var age by mutableStateOf(0)
    var language by mutableStateOf<AppLanguage?>(null)

    /* -------------------------------------------------
     * PROTECTION
     * ------------------------------------------------- */
    var protectionMode by mutableStateOf(ProtectionMode.NONE)
    var isSetupComplete by mutableStateOf(false)

    /* -------------------------------------------------
     * ONBOARDING FLOW FLAGS
     * ------------------------------------------------- */
    var hasSeenWelcome by mutableStateOf(false)
    var hasSeenModeExplanation by mutableStateOf(false)
    var hasAddedTrustedContacts by mutableStateOf(false)

    /* -------------------------------------------------
     * SERVICE / RUNTIME FLAGS
     * ------------------------------------------------- */
    var shouldStartService by mutableStateOf(false)
    var statusServiceStarted by mutableStateOf(false)

    var isProtectionServiceRunning by mutableStateOf(false)
    var isNotificationServiceRunning by mutableStateOf(false)


    /* -------------------------------------------------
     * SAFETY / FALLBACK INFO
     * ------------------------------------------------- */
    var lastDowngradeReason by mutableStateOf<String?>(null)

    /* -------------------------------------------------
     * TRUSTED SOURCES (USER CONTROLLED)
     * ------------------------------------------------- */
    var trustedContacts by mutableStateOf(mutableSetOf<String>())

    var trustedApps by mutableStateOf(
        mutableSetOf(
            "com.google.android.apps.nbu.paisa.user", // GPay
            "net.one97.paytm",                         // Paytm
            "com.phonepe.app"                          // PhonePe
        )
    )

    /* -------------------------------------------------
     * SOS / EMERGENCY (FUTURE SAFE)
     * ------------------------------------------------- */
    var sosEnabled by mutableStateOf(false)
    var sosTriggerCount by mutableStateOf(0)

    /* ---------------- FAMILY ALERTS ---------------- */

    var familyAlertsEnabled by mutableStateOf(false)

    /* -------------------------------------------------
     * DEBUG / RESET
     * ------------------------------------------------- */
    fun resetForDebug() {
        name = ""
        age = 0
        language = null
        protectionMode = ProtectionMode.NONE
        isSetupComplete = false

        hasSeenWelcome = false
        hasSeenModeExplanation = false
        hasAddedTrustedContacts = false

        shouldStartService = false
        statusServiceStarted = false

        isProtectionServiceRunning = false
        isNotificationServiceRunning = false

        lastDowngradeReason = null

        trustedContacts = mutableSetOf()
        trustedApps = mutableSetOf()

        sosEnabled = false
        sosTriggerCount = 0
    }
}