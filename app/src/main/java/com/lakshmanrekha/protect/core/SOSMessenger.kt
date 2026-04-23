package com.lakshmanrekha.protect.core

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import com.lakshmanrekha.protect.R
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ThreatLogger

object SOSMessenger {

    fun sendSOS(context: Context) {

        val contacts = AppState.trustedContacts
        if (contacts.isEmpty()) {
            ThreatLogger.logSystem("SOS SMS aborted: no trusted contacts")
            return
        }

        val message = buildMessage(context)

        // Use modern SmsManager retrieval
        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

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
                ThreatLogger.logSystem("SMS failed for $number: ${e.message}")
            }
        }

        ThreatLogger.logSystem("SOS SMS sent to ${contacts.size} contacts")
    }

    private fun buildMessage(context: Context): String {
        val name = AppState.name.ifBlank { context.getString(R.string.generic_user_name) }

        return """
            ${context.getString(R.string.sos_alert_header)}
            ${context.getString(R.string.sos_app_name)}

            ${context.getString(R.string.sos_user_danger, name)}
            ${context.getString(R.string.sos_contact_immediately)}
        """.trimIndent()
    }
}