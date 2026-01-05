package com.lakshmanrekha.protect.utils

import com.lakshmanrekha.protect.model.ThreatLevel

object RuntimeState {

    // --------------------
    // CALL / EVENT CONTEXT
    // --------------------
    var callOngoing: Boolean = false
    var currentCallerTrusted: Boolean = false


    // --------------------
    // SOURCE CONTEXT
    // --------------------
    var activeSourceApp: String? = null
    var activeSourceNumber: String? = null

    // --------------------
    // APP BEHAVIOR SIGNALS
    // --------------------
    var lastForegroundApp: String? = null
    var lastAppSwitchTime: Long = 0L
    var rapidAppSwitching: Boolean = false

    // --------------------
    // RISK SIGNALS
    // --------------------
    var upiOpenedDuringCall: Boolean = false
    var otpPatternDetected: Boolean = false

    // --------------------
    // MODE CONTROL FLAGS
    // --------------------
    var bypassProtection: Boolean = false
    var warningShown: Boolean = false
    var overlayShown: Boolean = false
    var emergencyTriggered: Boolean = false

    // --------------------
    // POST-EVENT SUMMARY (FIXED)
    // --------------------
    var lastThreatLevel: ThreatLevel? = null
    var lastThreatReasons: List<String> = emptyList()

    // --------------------
    // RESET AFTER EVENT
    // --------------------
    fun resetSession() {
        callOngoing = false
        currentCallerTrusted = false
        activeSourceApp = null
        activeSourceNumber = null
        lastForegroundApp = null
        rapidAppSwitching = false
        upiOpenedDuringCall = false
        otpPatternDetected = false
        bypassProtection = false
        warningShown = false
        overlayShown = false
        emergencyTriggered = false
        lastThreatLevel = null
        lastThreatReasons = emptyList()
    }
}