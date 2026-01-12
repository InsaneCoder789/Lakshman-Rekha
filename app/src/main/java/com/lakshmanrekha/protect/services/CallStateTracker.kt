package com.lakshmanrekha.protect.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.lakshmanrekha.protect.ui.PostCallSummaryActivity
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.RuntimeState

class CallStateTracker : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        when (state) {

            // 📞 Incoming or outgoing call detected
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val number =
                    intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                RuntimeState.callOngoing = true
                RuntimeState.activeSourceNumber = number

                RuntimeState.currentCallerTrusted =
                    number != null && AppState.trustedContacts.contains(number)
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                RuntimeState.callOngoing = true
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                if (RuntimeState.callOngoing) {
                    RuntimeState.callOngoing = false

                    // ✅ Show summary only if threat existed
                    if (RuntimeState.lastThreatLevel != null) {
                        val i = Intent(context, PostCallSummaryActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                    }

                    // ✅ Reset ONLY call-related flags
                    RuntimeState.resetCallFlags()
                }
            }
        }
    }
}