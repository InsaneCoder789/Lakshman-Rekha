package com.lakshmanrekha.protect.core

import android.content.Context
import android.content.Intent
import com.lakshmanrekha.protect.ui.CoachActivity

object CoachLauncher {

    fun launch(context: Context) {
        val intent = Intent(context, CoachActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}