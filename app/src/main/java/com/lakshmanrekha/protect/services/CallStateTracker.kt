package com.lakshmanrekha.protect.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.lakshmanrekha.protect.ui.PostCallSummaryActivity
import com.lakshmanrekha.protect.utils.RuntimeState

class CallStateTracker : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        when (state) {

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                RuntimeState.callOngoing = true
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                RuntimeState.callOngoing = false
                if (RuntimeState.lastCallThreatLevel != null) {
                    val summaryIntent =
                        Intent(context, PostCallSummaryActivity::class.java)
                    summaryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(summaryIntent)
                }

                // Reset transient states
                RuntimeState.rapidAppSwitching = false
                RuntimeState.upiOpenedDuringCall = false
                RuntimeState.otpPatternDetected = false
            }
        }
    }
}