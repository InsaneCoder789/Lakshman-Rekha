package com.lakshmanrekha.protect.modes

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lakshmanrekha.protect.R
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.ThreatLogger

object SaathiActions {

    private const val CHANNEL_ID = "saathi_channel"

    fun apply(context: Context, threat: Threat) {

        // 1️⃣ Always log
        ThreatLogger.logThreat(threat)

        // 2️⃣ Gentle notification (only for CAUTION+)
        if (threat.level >= ThreatLevel.CAUTION) {

            val text = when (threat.level) {
                ThreatLevel.CAUTION ->
                    "Please be careful. If someone asks for OTP, do not share."
                ThreatLevel.RISKY ->
                    "This looks risky. Consider stopping and asking family."
                ThreatLevel.DANGEROUS ->
                    "High risk detected. Do not make payments."
                else -> return
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Lakshman Rekha – Saathi")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        }
    }
}