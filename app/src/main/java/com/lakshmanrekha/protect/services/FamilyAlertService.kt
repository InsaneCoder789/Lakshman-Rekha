package com.lakshmanrekha.protect.services

import android.content.Context
import android.telephony.SmsManager
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.utils.*

object FamilyAlertService {

    fun notifyTrustedContacts(context: Context, threat: Threat) {

        if (!AppState.familyAlertsEnabled) return
        if (AppState.trustedContacts.isEmpty()) return

        val message = buildMessage(threat)

        val smsManager = SmsManager.getDefault()

        AppState.trustedContacts.forEach { number ->
            try {
                smsManager.sendTextMessage(number, null, message, null, null)
            } catch (e: Exception) {
                ThreatLogger.logSystem("Family alert failed for $number")
            }
        }

        ThreatLogger.logSystem("📣 Family alert sent (non-SOS)")
    }

    private fun buildMessage(threat: Threat): String {
        val reasons = threat.reasons.take(2).joinToString(", ")

        return """
🚨 Scam Warning Alert

Lakshman Rekha detected a HIGH-RISK situation.

Reasons:
$reasons

Please check on them.
(No personal data shared)
        """.trimIndent()
    }
}