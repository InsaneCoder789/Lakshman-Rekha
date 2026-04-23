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
import com.lakshmanrekha.protect.utils.RuntimeState
import com.lakshmanrekha.protect.utils.ThreatLogger

/**
 * SaathiActions
 *
 * PURPOSE:
 * - Gentle guidance
 * - NON-intrusive
 * - SINGLE evolving notification
 */
object SaathiActions {

    private const val CHANNEL_ID = "saathi_guidance_channel"
    private const val NOTIFICATION_ID = 2001

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun apply(context: Context, threat: Threat) {

        // 1️⃣ Always log threat (history)
        ThreatLogger.logThreat(threat)

        // 2️⃣ Handle SAFE level - Clear last threat if it's safe now
        if (threat.level == ThreatLevel.SAFE) {
            RuntimeState.lastThreatLevel = null
            // Optional: cancel notification if the threat is gone
            // NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
            return
        }

        // 3️⃣ Intelligent Filtering:
        // Allow update if:
        // - Level has increased (more dangerous)
        // - Reasons have changed significantly (new scam type)
        // - 10 seconds have passed since last update
        val lastLevel = RuntimeState.lastThreatLevel
        val lastReasons = RuntimeState.lastThreatReasons
        val now = System.currentTimeMillis()
        val timeSinceLast = now - threat.timestamp // Threat object has its own timestamp

        val isLevelHigher = lastLevel == null || threat.level.ordinal > lastLevel.ordinal
        val isNewReason = lastReasons.isEmpty() || threat.reasons.any { !lastReasons.contains(it) }

        if (!isLevelHigher && !isNewReason && timeSinceLast < 10000) {
            return
        }

        RuntimeState.lastThreatLevel = threat.level
        RuntimeState.lastThreatReasons = threat.reasons

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
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(false)       // Allow alert on meaningful updates
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Saathi Safety Guidance",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gentle scam safety guidance"
                setShowBadge(false)
            }

            manager.createNotificationChannel(channel)
        }
    }
}