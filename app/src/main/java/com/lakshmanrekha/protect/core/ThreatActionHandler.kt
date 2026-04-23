package com.lakshmanrekha.protect.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.reporting.CitizenReportManager
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.ThreatLogger
import java.net.URLEncoder

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

    /**
     * 🆕 WhatsApp Verification Feature
     * Sends the scam details to a trusted family member for confirmation.
     */
    fun verifyViaWhatsApp(context: Context, threat: Threat) {
        val number = AppState.trustedContacts.firstOrNull() ?: return
        val isHindi = LanguageManager.isHindi()

        val reasonsStr = threat.reasons.joinToString("\n- ")
        val source = threat.sourceNumber ?: threat.sourceApp ?: "Unknown"

        val message = if (isHindi) {
            """
            नमस्ते, मुझे लक्ष्मण रेखा ऐप से यह अलर्ट मिला है:
            
            ⚠️ खतरा: ${threat.level.name}
            📱 स्रोत: $source
            🚩 कारण:
            - $reasonsStr
            
            क्या यह सुरक्षित है? कृपया मुझे बताएं।
            """.trimIndent()
        } else {
            """
            Hi, I received this alert from Lakshman Rekha app:
            
            ⚠️ Threat: ${threat.level.name}
            📱 Source: $source
            🚩 Reasons:
            - $reasonsStr
            
            Is this safe? Please let me know.
            """.trimIndent()
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val url = "https://api.whatsapp.com/send?phone=$number&text=" + 
                         URLEncoder.encode(message, "UTF-8")
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ThreatLogger.logSystem("User shared threat details via WhatsApp")
        } catch (e: Exception) {
            ThreatLogger.logSystem("WhatsApp verification failed: ${e.message}")
        }
    }

    fun blockSource(context: Context, threat: Threat) {
        ThreatLogger.logSystem(
            "Block suggested for source: ${threat.sourceNumber ?: threat.sourceApp}"
        )
    }

    fun reportScam(threat: Threat) {
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