package com.lakshmanrekha.protect.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.lakshmanrekha.protect.R
import com.lakshmanrekha.protect.utils.Strings

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    /* =====================================================
     * OVERLAY UI
     * ===================================================== */

    private fun showOverlay(
        title: String,
        message: String,
        backgroundColor: Int
    ) {
        if (overlayView != null) return

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_warning, null)

        overlayView!!.findViewById<TextView>(R.id.overlayTitle).text = title
        overlayView!!.findViewById<TextView>(R.id.overlayMessage).text = message
        overlayView!!.setBackgroundColor(backgroundColor)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP

        windowManager.addView(overlayView, params)

        // Senior-friendly dismissal
        overlayView!!.setOnClickListener {
            stopSelf()
        }
    }

    /* =====================================================
     * SERVICE ENTRY
     * ===================================================== */

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val type = intent?.getStringExtra(KEY_TYPE) ?: return START_NOT_STICKY

        startForegroundIfNeeded(type)

        val (title, message, bgColor) =
            if (type == TYPE_EMERGENCY) {
                Triple(
                    Strings.protectionStatus(),
                    Strings.paymentWarning(),
                    0x55FF0000
                )
            } else {
                Triple(
                    Strings.protectionStatus(),
                    Strings.otpWarning(),
                    0x55FFF176
                )
            }

        showOverlay(title, message, bgColor.toInt())

        return START_NOT_STICKY
    }

    /* =====================================================
     * FOREGROUND (ANDROID 8+ SAFETY)
     * ===================================================== */

    private fun startForegroundIfNeeded(type: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channelId = "lakshmanrekha_overlay"
        val manager = getSystemService(NotificationManager::class.java)

        if (manager.getNotificationChannel(channelId) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Protection Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Emergency protection warnings"
                    setSound(null, null)
                }
            )
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(Strings.protectionStatus())
            .setContentText(
                if (type == TYPE_EMERGENCY)
                    "Emergency protection active"
                else
                    "Security warning active"
            )
            .setOngoing(true)
            .setSilent(true)
            .build()

        startForeground(101, notification)
    }

    /* =====================================================
     * PUBLIC API (CALLED FROM RakshaActions)
     * ===================================================== */

    companion object {

        private const val KEY_TYPE = "type"
        private const val TYPE_WARNING = "WARNING"
        private const val TYPE_EMERGENCY = "EMERGENCY"

        fun showWarning(context: Context) {
            start(context, TYPE_WARNING)
        }

        fun showEmergency(context: Context) {
            start(context, TYPE_EMERGENCY)
        }

        private fun start(context: Context, type: String) {
            val intent = Intent(context, OverlayService::class.java).apply {
                putExtra(KEY_TYPE, type)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}