package com.lakshmanrekha.protect.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lakshmanrekha.protect.R

object StartupNotifier {

    private const val CHANNEL_ID = "lakshmanrekha_status"

    fun showProtectionStarted(context: Context) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Protection Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Lakshman Rekha protection status"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lakshman Rekha")
            .setContentText("🛡️ Security protection is now active")
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}