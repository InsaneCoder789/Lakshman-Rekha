package com.lakshmanrekha.protect.utils

import androidx.compose.runtime.mutableStateListOf
import com.lakshmanrekha.protect.model.Threat
import java.text.SimpleDateFormat
import java.util.*

object ThreatLogger {

    // Using mutableStateListOf makes these observable by Jetpack Compose
    private val threats = mutableStateListOf<Threat>()
    private val systemLogs = mutableStateListOf<String>()

    fun logThreat(threat: Threat) {
        // Add to the top of the list
        threats.add(0, threat)

        // Keep the list manageable (e.g., last 50 threats)
        if (threats.size > 50) {
            threats.removeAt(threats.lastIndex)
        }
    }

    fun logSystem(message: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date())
        systemLogs.add(0, "[$time] $message")

        // Keep the last 20 logs
        if (systemLogs.size > 20) {
            systemLogs.removeAt(systemLogs.lastIndex)
        }
    }

    /**
     * Now this returns the actual observable list.
     * Recomposition will trigger automatically in HomeConsoleScreen
     * when logThreat or clearThreats is called.
     */
    fun getThreats(): List<Threat> = threats

    fun getSystemLogs(): List<String> = systemLogs

    /**
     * Clears the threat history.
     * This was the missing function causing your error.
     */
    fun clearThreats() {
        threats.clear()
        logSystem("Threat history cleared by user")
    }
}