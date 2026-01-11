package com.lakshmanrekha.protect.modes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lakshmanrekha.protect.R
import com.lakshmanrekha.protect.model.Threat
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.ThreatLogger

object SaathiActions {

    private const val CHANNEL_ID = "saathi_channel"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun apply(context: Context, threat: Threat) {

        // 1️⃣ Always log
        ThreatLogger.logThreat(threat)

        // 2️⃣ Only notify if CAUTION+
        if (threat.level < ThreatLevel.CAUTION) return

        ensureChannel(context)

        val isHindi = LanguageManager.isHindi()

        val message = when (threat.level) {
            ThreatLevel.CAUTION ->
                if (isHindi)
                    "सावधान रहें। OTP कभी साझा न करें।"
                else
                    "Be careful. Never share OTP."

            ThreatLevel.RISKY ->
                if (isHindi)
                    "यह जोखिम भरा लग रहा है। परिवार से पुष्टि करें।"
                else
                    "This looks risky. Please verify with family."

            ThreatLevel.DANGEROUS ->
                if (isHindi)
                    "उच्च खतरा। कोई भुगतान न करें।"
                else
                    "High risk detected. Do NOT make payments."

            else -> return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lakshman Rekha – Saathi")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Saathi Guidance",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }
}