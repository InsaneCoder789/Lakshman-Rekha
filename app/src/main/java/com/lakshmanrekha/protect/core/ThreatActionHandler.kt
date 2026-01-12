package com.lakshmanrekha.protect.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.reporting.CitizenReportManager
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ThreatLogger

object ThreatActionHandler {

    fun callTrustedContact(context: Context) {
        val number = AppState.trustedContacts.firstOrNull() ?: return

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)

        ThreatLogger.logSystem("User called trusted contact")
    }

    fun blockSource(context: Context, threat: Threat) {
        // Demo-safe: log only (real blocking requires default dialer)
        ThreatLogger.logSystem(
            "Block suggested for source: ${threat.sourceNumber ?: threat.sourceApp}"
        )
    }

    fun reportScam(threat: Threat) {
        // Privacy-safe reporting (no personal data)
        ThreatLogger.logSystem(
            "Scam reported: ${threat.level} | ${threat.reasons.joinToString()}"

        )
        CitizenReportManager.report(threat)
    }

    fun verifyMerchant(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.consumerhelpline.gov.in")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)

        ThreatLogger.logSystem("User opened merchant verification")
    }
}