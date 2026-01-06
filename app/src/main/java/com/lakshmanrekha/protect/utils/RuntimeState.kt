package com.lakshmanrekha.protect.utils

import com.lakshmanrekha.protect.ml.ScamRiskModel
import com.lakshmanrekha.protect.model.ThreatLevel

/**
 * Runtime-only state holder.
 * ❗ NOTHING here is persisted to disk.
 * ❗ Everything resets per session / call lifecycle.
 *
 * This object is the single source of truth for:
 * - Call context
 * - Risk signals
 * - ML model access
 * - Post-call summary state
 */
object RuntimeState {

    /* =========================================================
     * CALL / EVENT CONTEXT
     * ========================================================= */

    /** True while a phone / VoIP call is active */
    var callOngoing: Boolean = false

    /** Whether current caller is in trusted contacts */
    var currentCallerTrusted: Boolean = false


    /* =========================================================
     * SOURCE CONTEXT
     * ========================================================= */

    /** Foreground app that triggered detection */
    var activeSourceApp: String? = null

    /** Active phone number (if available) */
    var activeSourceNumber: String? = null


    /* =========================================================
     * APP BEHAVIOR SIGNALS
     * ========================================================= */

    /** Last foreground app name */
    var lastForegroundApp: String? = null

    /** Timestamp of last app switch */
    var lastAppSwitchTime: Long = 0L

    /** Rapid switching between apps detected */
    var rapidAppSwitching: Boolean = false


    /* =========================================================
     * RISK SIGNALS
     * ========================================================= */

    /** UPI / payment app opened during call */
    var upiOpenedDuringCall: Boolean = false

    /** OTP / sensitive pattern detected */
    var otpPatternDetected: Boolean = false


    /* =========================================================
     * MODE CONTROL FLAGS
     * ========================================================= */

    /** User explicitly bypassed protection */
    var bypassProtection: Boolean = false

    /** Warning UI already shown */
    var warningShown: Boolean = false

    /** Overlay currently visible */
    var overlayShown: Boolean = false

    /** SOS / emergency flow triggered */
    var emergencyTriggered: Boolean = false


    /* =========================================================
     * POST-CALL SUMMARY (CRITICAL SECTION)
     * ========================================================= */

    /**
     * Final evaluated threat level for the last call / event.
     * Set by ProtectionManager.
     */
    var lastThreatLevel: ThreatLevel? = null

    /**
     * Human-readable reasons explaining the threat.
     * Set by ScamDetector / ProtectionManager.
     */
    var lastThreatReasons: List<String> = emptyList()

    /**
     * ✅ SINGLE-FIRE FLAG
     * Controls whether PostCallSummary UI should be shown.
     *
     * This prevents:
     * - Re-showing summary on app reopen
     * - Re-triggering after rotation / resume
     */
    var postCallSummaryPending: Boolean = false


    /* =========================================================
     * ML MODEL (SINGLETON)
     * ========================================================= */

    /**
     * Lightweight on-device ML model.
     * Initialized ONCE in MainActivity.
     */
    var scamRiskModel: ScamRiskModel? = null


    /* =========================================================
     * RESET LOGIC
     * ========================================================= */

    /**
     * Reset all runtime flags AFTER post-call summary is dismissed.
     * ❗ Do NOT call this when a call ends.
     * ❗ Call only after summary UI is closed.
     */
    fun resetSession() {

        // Call state
        callOngoing = false
        currentCallerTrusted = false

        // Source context
        activeSourceApp = null
        activeSourceNumber = null

        // App behavior
        lastForegroundApp = null
        lastAppSwitchTime = 0L
        rapidAppSwitching = false

        // Risk signals
        upiOpenedDuringCall = false
        otpPatternDetected = false

        // Mode flags
        bypassProtection = false
        warningShown = false
        overlayShown = false
        emergencyTriggered = false

        // Post-call summary state
        lastThreatLevel = null
        lastThreatReasons = emptyList()
        postCallSummaryPending = false
    }
}