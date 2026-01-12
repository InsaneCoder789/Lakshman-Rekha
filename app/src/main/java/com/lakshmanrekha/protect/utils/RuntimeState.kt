package com.lakshmanrekha.protect.utils

import com.lakshmanrekha.protect.ml.ScamRiskModel
import com.lakshmanrekha.protect.model.ThreatLevel

object RuntimeState {

    /* ================= CALL CONTEXT ================= */

    var callOngoing: Boolean = false
    var currentCallerTrusted: Boolean = false

    /* ================= SOURCE CONTEXT ================= */

    var activeSourceApp: String? = null
    var activeSourceNumber: String? = null

    /* ================= APP BEHAVIOR ================= */

    var lastForegroundApp: String? = null
    var lastAppSwitchTime: Long = 0L
    var rapidAppSwitching: Boolean = false

    /* ================= RISK SIGNALS ================= */

    var upiOpenedDuringCall: Boolean = false
    var otpPatternDetected: Boolean = false

    /* ================= MODE FLAGS ================= */

    var bypassProtection: Boolean = false
    var warningShown: Boolean = false
    var overlayShown: Boolean = false
    var emergencyTriggered: Boolean = false
    var coachShown: Boolean = false

    /* ================= POST-CALL SUMMARY ================= */

    var lastThreatLevel: ThreatLevel? = null
    var lastThreatReasons: List<String> = emptyList()
    var postCallSummaryPending: Boolean = false

    /* ================= ML MODEL ================= */

    var scamRiskModel: ScamRiskModel? = null

    /* ==================================================
     * RESET FUNCTIONS (VERY IMPORTANT)
     * ================================================== */

    /** Reset ONLY call-related flags (used by CallStateTracker) */
    fun resetCallFlags() {
        callOngoing = false
        currentCallerTrusted = false
        upiOpenedDuringCall = false
        otpPatternDetected = false
        rapidAppSwitching = false
        lastForegroundApp = null
        lastAppSwitchTime = 0L
        activeSourceApp = null
        activeSourceNumber = null
    }

    /** Reset EVERYTHING (call AFTER summary screen is dismissed) */
    fun resetSession() {
        resetCallFlags()

        bypassProtection = false
        warningShown = false
        overlayShown = false
        emergencyTriggered = false
        coachShown = false

        lastThreatLevel = null
        lastThreatReasons = emptyList()
        postCallSummaryPending = false
    }
}