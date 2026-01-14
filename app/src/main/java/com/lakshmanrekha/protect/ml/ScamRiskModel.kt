package com.lakshmanrekha.protect.ml

import android.content.Context
import android.util.Log
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

        // 🚫 Never run ML on junk
        if (text.length < 10) {
            return ScamSignals.safeFallback("Text too short for ML")
        }

        return try {
            runInference(text)
        } catch (e: Exception) {
            Log.e("ScamRiskModel", "ML failed: ${e.message}")
            ScamSignals.safeFallback("ML inference failed")
        }
    }

    /* ======================================================
     * INFERENCE
     * ====================================================== */

    private fun runInference(text: String): ScamSignals {

        val input = tokenize(text)

        val outputMap = mutableMapOf<Int, Any>()

        for (i in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(i)
            val shape = tensor.shape() // always [1, N]
            outputMap[i] = Array(shape[0]) { FloatArray(shape[1]) }
        }

        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputMap)

        val outputs = outputMap.values
            .map { it as Array<FloatArray> }
            .associateBy { it[0].size }

        val scamScore = outputs[1]?.get(0)?.get(0) ?: 0f
        val isScam = scamScore > 0.5f

        val severityVec = outputs[5]?.get(0)
        val severity = severityVec?.argMax()?.plus(1) ?: 1

        val stageVec = outputs[3]?.get(0)
        val stage = ScamStage.values()
            .getOrElse(stageVec?.argMax() ?: 0) { ScamStage.LURE }

        val actionVec = outputs[6]?.get(0)
        val action = ScamAction.values()
            .getOrElse(actionVec?.argMax() ?: 0) { ScamAction.UNKNOWN }

        val flags = outputs[5]?.get(0) ?: FloatArray(5)

        val hasOtp = flags.getOrNull(0)?.let { it > 0.5f } ?: false
        val hasUpi = flags.getOrNull(1)?.let { it > 0.5f } ?: false
        val hasUrl = flags.getOrNull(2)?.let { it > 0.5f } ?: false
        val hasThreat = flags.getOrNull(3)?.let { it > 0.5f } ?: false
        val hasUrgency = flags.getOrNull(4)?.let { it > 0.5f } ?: false

        val confidence = max(scamScore, severityVec?.maxOrNull() ?: 0f)

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
            explanation = buildExplanation(
                hasOtp, hasUpi, hasUrl, hasThreat, hasUrgency
            )
        )
    }

    /* ======================================================
     * TOKENIZATION (FLOAT32)
     * ====================================================== */

    private fun tokenize(text: String): Array<FloatArray> {
        val tokens = text.lowercase()
            .split(Regex("\\s+"))
            .map { wordIndex[it] ?: 1 }
            .takeLast(maxLen)

        val padded = FloatArray(maxLen)
        val start = maxLen - tokens.size
        tokens.forEachIndexed { i, v ->
            padded[start + i] = v.toFloat()
        }

        return arrayOf(padded)
    }

    /* ======================================================
     * HELPERS
     * ====================================================== */

    private fun FloatArray.argMax(): Int =
        indices.maxByOrNull { this[it] } ?: 0

    private fun buildExplanation(
        otp: Boolean,
        upi: Boolean,
        url: Boolean,
        threat: Boolean,
        urgency: Boolean
    ): String =
        listOfNotNull(
            "OTP mentioned".takeIf { otp },
            "Payment request detected".takeIf { upi },
            "Suspicious link detected".takeIf { url },
            "Threatening language detected".takeIf { threat },
            "Urgency pressure detected".takeIf { urgency }
        ).ifEmpty { listOf("No strong scam indicators detected") }
            .joinToString(", ")

    /* ======================================================
     * LOADERS
     * ====================================================== */

    private fun loadModel(context: Context): ByteBuffer {
        val afd = context.assets.openFd("scam_signal.tflite")
        return afd.createInputStream().channel.map(
            FileChannel.MapMode.READ_ONLY,
            afd.startOffset,
            afd.declaredLength
        ).order(ByteOrder.nativeOrder())
    }

    private fun loadTokenizer(context: Context): Map<String, Int> {
        val json = context.assets.open("tokenizer.json")
            .bufferedReader().use { it.readText() }

        val root = JSONObject(json)
        val wordIndexJson = JSONObject(
            root.getJSONObject("config").getString("word_index")
        )

        return wordIndexJson.keys().asSequence()
            .associateWith { wordIndexJson.getInt(it) }
    }
}