package com.lakshmanrekha.protect.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.lakshmanrekha.protect.ui.PostCallSummaryActivity
import com.lakshmanrekha.protect.utils.RuntimeState

class CallStateTracker : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        when (state) {

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                RuntimeState.callOngoing = true
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                if (RuntimeState.callOngoing) {
                    RuntimeState.callOngoing = false

                    // Show summary ONLY if something was detected
                    if (RuntimeState.lastThreatLevel != null) {
                        val i = Intent(context, PostCallSummaryActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                    }

                    RuntimeState.resetCallFlags()
                }
            }
        }
    }
}