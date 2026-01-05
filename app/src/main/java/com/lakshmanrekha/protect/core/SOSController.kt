package com.lakshmanrekha.protect.core

import android.content.Context
import android.os.SystemClock
import com.lakshmanrekha.protect.services.SOSService
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ThreatLogger

object SOSController {

    private const val REQUIRED_PRESSES = 3
    private const val WINDOW_MS = 3000L

    private val presses = mutableListOf<Long>()

    fun onVolumePressed(context: Context) {

        val now = SystemClock.elapsedRealtime()
        presses.add(now)

        // Remove old presses
        presses.removeAll { now - it > WINDOW_MS }

        if (presses.size >= REQUIRED_PRESSES) {
            presses.clear()
            triggerSOS(context)
        }
    }

    private fun triggerSOS(context: Context) {

        if (AppState.trustedContacts.isEmpty()) {
            ThreatLogger.logSystem("SOS blocked: no trusted contacts")
            return
        }

        ThreatLogger.logSystem("🚨 SOS TRIGGERED")

        SOSService.start(context)
    }
}