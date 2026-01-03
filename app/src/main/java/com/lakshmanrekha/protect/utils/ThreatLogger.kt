package com.lakshmanrekha.protect.utils

import com.lakshmanrekha.protect.model.Threat
import java.text.SimpleDateFormat
import java.util.*

object ThreatLogger {

    private val threats = mutableListOf<Threat>()
    private val systemLogs = mutableListOf<String>()

    fun logThreat(threat: Threat) {
        threats.add(0, threat)
        if (threats.size > 50) threats.removeLast()
    }

    fun logSystem(message: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date())
        systemLogs.add(0, "[$time] $message")
        if (systemLogs.size > 20) systemLogs.removeLast()
    }

    fun getThreats(): List<Threat> = threats.toList()

    fun getSystemLogs(): List<String> = systemLogs.toList()
}