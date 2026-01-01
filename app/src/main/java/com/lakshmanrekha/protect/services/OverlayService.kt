package com.lakshmanrekha.protect.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.view.View
import com.lakshmanrekha.protect.R

class OverlayService : Service() {

    private var overlayView: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = intent?.getStringExtra("overlay_message") ?: "⚠️ Unknown warning"
        showOverlay(message)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showOverlay(warningText: String) {
        if (overlayView != null) return

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)

        overlayView = inflater.inflate(R.layout.overlay_warning, null)
        overlayView?.findViewById<TextView>(R.id.overlayText)?.text = warningText

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP
        params.y = 60

        wm.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it)
            overlayView = null
        }
    }
}