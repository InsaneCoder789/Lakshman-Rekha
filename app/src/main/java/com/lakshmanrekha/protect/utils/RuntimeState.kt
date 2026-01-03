package com.lakshmanrekha.protect.utils

import com.lakshmanrekha.protect.model.ThreatLevel

object RuntimeState {

    // Call context
    var callOngoing: Boolean = false
    var currentCallerTrusted: Boolean = false

    // App behavior
    var lastForegroundApp: String? = null
    var lastAppSwitchTime: Long = 0L
    var rapidAppSwitching: Boolean = false

    // Risk signals
    var upiOpenedDuringCall: Boolean = false
    var otpPatternDetected: Boolean = false

    // Post-call summary
    var lastCallThreatLevel: ThreatLevel? = null
    var lastCallReasons: List<String> = emptyList()
}