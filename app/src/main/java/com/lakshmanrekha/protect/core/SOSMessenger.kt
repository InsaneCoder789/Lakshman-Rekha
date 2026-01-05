package com.lakshmanrekha.protect.core

import android.content.Context
import android.telephony.SmsManager
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ThreatLogger

object SOSMessenger {

    fun sendSOS(context: Context) {

        val contacts = AppState.trustedContacts
        if (contacts.isEmpty()) {
            ThreatLogger.logSystem("SOS SMS aborted: no trusted contacts")
            return
        }

        val message = buildMessage()

        val smsManager = SmsManager.getDefault()

        contacts.forEach { number ->
            try {
                smsManager.sendTextMessage(
                    number,
                    null,
                    message,
                    null,
                    null
                )
            } catch (e: Exception) {
                ThreatLogger.logSystem("SMS failed for $number")
            }
        }

        ThreatLogger.logSystem("SOS SMS sent to ${contacts.size} contacts")
    }

    private fun buildMessage(): String {
        val name = AppState.name.ifBlank { "A Lakshman Rekha user" }

        return """
            🚨 EMERGENCY ALERT 🚨
            This is Lakshman Rekha.

            $name may be in danger.
            Please contact immediately.
        """.trimIndent()
    }
}