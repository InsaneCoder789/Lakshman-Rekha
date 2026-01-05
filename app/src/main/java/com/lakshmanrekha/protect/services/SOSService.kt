package com.lakshmanrekha.protect.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Vibrator
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.ui.SOSActivity
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.ThreatLogger

class SOSService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 🔊 Strong vibration alert
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(longArrayOf(0, 500, 300, 500), -1)

        // 📩 Send SOS messages
        sendSosMessages()

        // 🚨 Launch SOS UI
        val sosIntent = Intent(this, SOSActivity::class.java)
        sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(sosIntent)

        stopSelf()
        return START_NOT_STICKY
    }

    private fun sendSosMessages() {
        if (AppState.trustedContacts.isEmpty()) {
            ThreatLogger.logSystem("SOS aborted: no trusted contacts")
            return
        }

        val smsManager = SmsManager.getDefault()

        val message =
            "🚨 EMERGENCY ALERT 🚨\n" +
                    "I may be in danger. Please try to contact me immediately.\n" +
                    "— Sent via Lakshman Rekha"

        AppState.trustedContacts.forEach { number ->
            try {
                smsManager.sendTextMessage(number, null, message, null, null)
            } catch (e: Exception) {
                ThreatLogger.logSystem("Failed to send SOS SMS to $number")
            }
        }

        ThreatLogger.logSystem("SOS SMS sent to trusted contacts")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: android.content.Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, SOSService::class.java)
            )
        }
    }
}