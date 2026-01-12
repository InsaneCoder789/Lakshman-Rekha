package com.lakshmanrekha.protect.ml

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.max

class ScamRiskModel(context: Context) {

    private val interpreter: Interpreter
    private val wordIndex: Map<String, Int>
    private val maxLen = 40

    init {
        interpreter = Interpreter(loadModel(context))
        wordIndex = loadTokenizer(context)
    }

    /* ======================================================
     * PUBLIC API
     * ====================================================== */

    fun predict(text: String): ScamSignals {

        val input = tokenize(text)

        // ---- OUTPUT BUFFERS (ORDER MUST MATCH TRAINING) ----
        val outIsScam = Array(1) { FloatArray(1) }
        val outSeverity = Array(1) { FloatArray(5) }
        val outStage = Array(1) { FloatArray(3) }
        val outAction = Array(1) { FloatArray(6) } // UNKNOWN handled in code

        val outOtp = Array(1) { FloatArray(1) }
        val outUpi = Array(1) { FloatArray(1) }
        val outUrl = Array(1) { FloatArray(1) }
        val outThreat = Array(1) { FloatArray(1) }
        val outUrgency = Array(1) { FloatArray(1) }

        interpreter.runForMultipleInputsOutputs(
            arrayOf(input),
            mapOf(
                0 to outIsScam,
                1 to outSeverity,
                2 to outStage,
                3 to outAction,
                4 to outOtp,
                5 to outUpi,
                6 to outUrl,
                7 to outThreat,
                8 to outUrgency
            )
        )

        // ---- PARSE OUTPUTS SAFELY ----
        val isScamScore = outIsScam[0][0]
        val isScam = isScamScore > 0.5f

        val severity = outSeverity[0].argMax() + 1

        val stageIndex = outStage[0].argMax()
        val stage = ScamStage.values().getOrElse(stageIndex) {
            ScamStage.LURE
        }

        val actionIndex = outAction[0].argMax()
        val action = ScamAction.values().getOrElse(actionIndex) {
            ScamAction.UNKNOWN
        }

        val hasOtp = outOtp[0][0] > 0.5f
        val hasUpi = outUpi[0][0] > 0.5f
        val hasUrl = outUrl[0][0] > 0.5f
        val hasThreat = outThreat[0][0] > 0.5f
        val hasUrgency = outUrgency[0][0] > 0.5f

        val confidence = max(
            isScamScore,
            outSeverity[0].maxOrNull() ?: 0f
        )

        val explanation = buildExplanation(
            hasOtp, hasUpi, hasUrl, hasThreat, hasUrgency
        )

        return ScamSignals(
            isScam = isScam,
            severity = severity,
            scamType = null,
            scamStage = stage,
            requestedAction = action,
            hasOtp = hasOtp,
            hasUpi = hasUpi,
            hasUrl = hasUrl,
            hasThreat = hasThreat,
            hasUrgency = hasUrgency,
            confidence = confidence,
            explanation = explanation
        )
    }

    /* ======================================================
     * TOKENIZATION
     * ====================================================== */

    private fun tokenize(text: String): Array<IntArray> {
        val tokens = text.lowercase()
            .split(Regex("\\s+"))
            .mapNotNull { wordIndex[it] }
            .takeLast(maxLen)

        val padded = IntArray(maxLen)
        val start = maxLen - tokens.size
        tokens.forEachIndexed { i, v ->
            padded[start + i] = v
        }

        return arrayOf(padded)
    }

    /* ======================================================
     * HELPERS
     * ====================================================== */

    private fun buildExplanation(
        otp: Boolean,
        upi: Boolean,
        url: Boolean,
        threat: Boolean,
        urgency: Boolean
    ): String {
        val reasons = mutableListOf<String>()
        if (otp) reasons.add("OTP mentioned")
        if (upi) reasons.add("Payment request detected")
        if (url) reasons.add("Suspicious link detected")
        if (threat) reasons.add("Threatening language detected")
        if (urgency) reasons.add("Urgency pressure detected")

        return if (reasons.isEmpty())
            "No obvious scam signals detected."
        else
            reasons.joinToString(", ")
    }

    private fun FloatArray.argMax(): Int =
        indices.maxByOrNull { this[it] } ?: 0

    /* ======================================================
     * LOADERS
     * ====================================================== */

    private fun loadModel(context: Context): ByteBuffer {
        val afd = context.assets.openFd("scam_signal.tflite")
        val input = afd.createInputStream()
        val channel = input.channel
        return channel.map(
            FileChannel.MapMode.READ_ONLY,
            afd.startOffset,
            afd.declaredLength
        ).order(ByteOrder.nativeOrder())
    }

    private fun loadTokenizer(context: Context): Map<String, Int> {

        val json = context.assets
            .open("tokenizer.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(json)
        val config = root.getJSONObject("config")

        // 🚨 word_index is a STRING, not an object
        val wordIndexString = config.getString("word_index")

        val wordIndexJson = JSONObject(wordIndexString)

        val map = mutableMapOf<String, Int>()
        val keys = wordIndexJson.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = wordIndexJson.getInt(key)
        }

        return map
    }
}