package com.lakshmanrekha.protect.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.lakshmanrekha.protect.R
import com.lakshmanrekha.protect.utils.ProtectionMode
import com.lakshmanrekha.protect.utils.AppState

object ProtectionNotifier {

    private const val CHANNEL_ID = "lakshman_rekha_status"
    private const val NOTIFICATION_ID = 999

    fun show(context: Context, mode: ProtectionMode) {

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Protection Status",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)

            fun restoreIfNeeded(context: Context) {
                if (AppState.protectionMode != ProtectionMode.NONE) {
                    show(context, AppState.protectionMode)
                }
            }
        }

        val text = when (mode) {
            ProtectionMode.SAATHI -> "🟢 Saathi protection active"
            ProtectionMode.LAKSHMAN -> "🟡 Lakshman protection active"
            ProtectionMode.RAKSHA -> "🔴 Raksha protection active"
            ProtectionMode.NONE -> "Protection inactive"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lakshman Rekha")
            .setContentText(text)
            .setOngoing(true)
            .build()

        // 🔁 SAME ID → notification updates, not duplicates
        manager.notify(NOTIFICATION_ID, notification)
    }
}