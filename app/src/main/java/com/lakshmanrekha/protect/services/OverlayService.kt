package com.lakshmanrekha.protect.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
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
        super.onDestroy()
    }

    private fun showOverlay(
        title: String,
        message: String,
        backgroundColor: Int
    ) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_warning, null)

        val titleView = overlayView!!.findViewById<TextView>(R.id.overlayTitle)
        val messageView = overlayView!!.findViewById<TextView>(R.id.overlayMessage)

        titleView.text = title
        messageView.text = message

        overlayView!!.setBackgroundColor(backgroundColor)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP
        windowManager.addView(overlayView, params)

        // Tap anywhere to dismiss (senior friendly)
        overlayView!!.setOnClickListener {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val type = intent?.getStringExtra("type") ?: return START_NOT_STICKY

        val title: String
        val message: String
        val bgColor: Int

        when (type) {
            "EMERGENCY" -> {
                title = Strings.protectionStatus()
                message = Strings.paymentWarning()
                bgColor = 0x55FF0000
            }

            else -> { // WARNING
                title = Strings.protectionStatus()
                message = Strings.otpWarning()
                bgColor = 0x55FFF176
            }
        }

        showOverlay(title, message, bgColor.toInt())

        return START_NOT_STICKY
    }

    companion object {

        fun showWarning(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            intent.putExtra("type", "WARNING")
            context.startService(intent)
        }

        fun showEmergency(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            intent.putExtra("type", "EMERGENCY")
            context.startService(intent)
        }
    }
}