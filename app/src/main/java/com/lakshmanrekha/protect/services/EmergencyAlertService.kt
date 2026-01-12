package com.lakshmanrekha.protect.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.R

class EmergencyAlertService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        triggerAlert()
        return START_NOT_STICKY
    }

    private fun triggerAlert() {

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // 📳 Vibration
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 1000, 500, 1000),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 1000, 500, 1000), -1)
        }

        // 🔔 MUST be called within 5 seconds
        startForeground(
            NOTIFICATION_ID,
            buildNotification(soundUri)
        )
    }

    private fun buildNotification(soundUri: android.net.Uri): Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Raksha Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🚨 SCAM ALERT")
            .setContentText("High risk detected. Do NOT share OTP or make payments.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "raksha_emergency"
        private const val NOTIFICATION_ID = 1001

        fun trigger(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, EmergencyAlertService::class.java)
            )
        }
    }
}