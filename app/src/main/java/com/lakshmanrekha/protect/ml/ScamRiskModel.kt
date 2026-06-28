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

    private companion object {
        const val OUTPUT_IS_SCAM = 0
        const val OUTPUT_SEVERITY = 1
        const val OUTPUT_STAGE = 2
        const val OUTPUT_ACTION = 3
        const val OUTPUT_HAS_OTP = 4
        const val OUTPUT_HAS_UPI = 5
        const val OUTPUT_HAS_URL = 6
        const val OUTPUT_HAS_THREAT = 7
        const val OUTPUT_HAS_URGENCY = 8
        const val EXPECTED_OUTPUT_TENSORS = 9
    }

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
        require(interpreter.outputTensorCount == EXPECTED_OUTPUT_TENSORS) {
            "Unexpected model outputs: ${interpreter.outputTensorCount}"
        }

        val outputMap = mutableMapOf<Int, Any>()

        for (i in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(i)
            val shape = tensor.shape() // always [1, N]
            outputMap[i] = Array(shape[0]) { FloatArray(shape[1]) }
        }

        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputMap)

        val scamScore = outputAt(outputMap, OUTPUT_IS_SCAM).getOrElse(0) { 0f }
        val isScam = scamScore > 0.5f

        val severityVec = outputAt(outputMap, OUTPUT_SEVERITY)
        val severity = severityVec?.argMax()?.plus(1) ?: 1

        val stageVec = outputAt(outputMap, OUTPUT_STAGE)
        val stage = ScamStage.values()
            .getOrElse(stageVec?.argMax() ?: 0) { ScamStage.LURE }

        val actionVec = outputAt(outputMap, OUTPUT_ACTION)
        val action = ScamAction.values()
            .getOrElse(actionVec?.argMax() ?: 0) { ScamAction.UNKNOWN }

        val hasOtp = outputScalar(outputMap, OUTPUT_HAS_OTP) > 0.5f
        val hasUpi = outputScalar(outputMap, OUTPUT_HAS_UPI) > 0.5f
        val hasUrl = outputScalar(outputMap, OUTPUT_HAS_URL) > 0.5f
        val hasThreat = outputScalar(outputMap, OUTPUT_HAS_THREAT) > 0.5f
        val hasUrgency = outputScalar(outputMap, OUTPUT_HAS_URGENCY) > 0.5f

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

    private fun outputAt(
        outputMap: Map<Int, Any>,
        index: Int
    ): FloatArray? = (outputMap[index] as? Array<FloatArray>)?.getOrNull(0)

    private fun outputScalar(
        outputMap: Map<Int, Any>,
        index: Int
    ): Float = outputAt(outputMap, index)?.getOrElse(0) { 0f } ?: 0f

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
