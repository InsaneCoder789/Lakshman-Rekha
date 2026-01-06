package com.lakshmanrekha.protect.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lakshmanrekha.protect.model.ThreatLevel
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme
import com.lakshmanrekha.protect.utils.LanguageManager
import com.lakshmanrekha.protect.utils.RuntimeState
import java.util.*

class PostCallSummaryActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        setContent {
            LakshmanRekhaTheme {
                PostCallSummaryScreen {
                    stopTTS()
                    RuntimeState.resetSession()
                    finish()
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language =
                if (LanguageManager.isHindi()) Locale("hi", "IN")
                else Locale("en", "IN")

            speakSummary()
        }
    }

    private fun speakSummary() {
        val level = RuntimeState.lastThreatLevel ?: ThreatLevel.SAFE
        val reasons = RuntimeState.lastThreatReasons.joinToString(". ")

        val message =
            if (LanguageManager.isHindi()) {
                when (level) {
                    ThreatLevel.DANGEROUS ->
                        "चेतावनी। यह कॉल बहुत खतरनाक थी। $reasons। कभी भी ओटीपी साझा न करें।"

                    ThreatLevel.RISKY ->
                        "सावधान। इस कॉल में धोखाधड़ी के संकेत मिले। $reasons।"

                    ThreatLevel.CAUTION ->
                        "यह कॉल संदिग्ध थी। $reasons।"

                    else ->
                        "यह कॉल सुरक्षित थी।"
                }
            } else {
                when (level) {
                    ThreatLevel.DANGEROUS ->
                        "Warning. This call was very risky. $reasons. Never share OTP or payment details."

                    ThreatLevel.RISKY ->
                        "Caution. This call showed scam signs. $reasons."

                    ThreatLevel.CAUTION ->
                        "This call looked suspicious. $reasons."

                    else ->
                        "This call was safe."
                }
            }

        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "POST_CALL_SUMMARY")
    }

    private fun stopTTS() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }

    override fun onDestroy() {
        stopTTS()
        super.onDestroy()
    }
}