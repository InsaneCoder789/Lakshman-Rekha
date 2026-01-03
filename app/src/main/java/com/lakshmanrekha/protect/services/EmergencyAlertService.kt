package com.lakshmanrekha.protect.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.lakshmanrekha.protect.R

class EmergencyAlertService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        triggerAlert(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        triggerAlert(this)
        return START_NOT_STICKY
    }

    private fun triggerAlert(context: Context) {

        // 🔊 Sound
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

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
            vibrator.vibrate(longArrayOf(0, 1000, 500, 1000), -1)
        }

        // 🔔 Notification
        val notification = buildNotification(context, ringtone)

        startForeground(1001, notification)
    }

    private fun buildNotification(
        context: Context,
        soundUri: android.net.Uri
    ): Notification {

        val channelId = "raksha_emergency"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Raksha Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(
                soundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🚨 SCAM ALERT")
            .setContentText("High risk detected. Do NOT share OTP or make payments.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    companion object {

        fun trigger(context: Context) {
            val intent = Intent(context, EmergencyAlertService::class.java)
            context.startService(intent)
        }
    }
}