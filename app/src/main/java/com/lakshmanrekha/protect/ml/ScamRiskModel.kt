package com.lakshmanrekha.protect.ml

import android.content.Context
import android.util.Log
import com.lakshmanrekha.protect.utils.ThreatLogger
import org.json.JSONArray
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ScamRiskModel(context: Context) {

    private val interpreter: Interpreter
    private val tokenizer: TokenizerConfig
    private val metadata: ModelMetadata

    private companion object {
        const val OUTPUT_IS_SCAM = 0
        const val OUTPUT_SEVERITY = 1
        const val OUTPUT_STAGE = 2
        const val OUTPUT_ACTION = 3
        const val OUTPUT_SCAM_TYPE = 4
        const val OUTPUT_HAS_OTP = 5
        const val OUTPUT_HAS_UPI = 6
        const val OUTPUT_HAS_URL = 7
        const val OUTPUT_HAS_QR = 8
        const val OUTPUT_HAS_PHONE = 9
        const val OUTPUT_HAS_THREAT = 10
        const val OUTPUT_HAS_URGENCY = 11
        const val EXPECTED_OUTPUT_TENSORS = 12

        private val urlRegex = Regex("(https?://\\S+|www\\.\\S+)", RegexOption.IGNORE_CASE)
        private val upiRegex = Regex("\\b[a-z0-9._-]{2,}@[a-z]{2,}\\b", RegexOption.IGNORE_CASE)
        private val phoneRegex = Regex("(?<!\\w)(?:\\+91[-\\s]?)?\\d[\\d\\-\\s]{7,}\\d(?!\\w)")
        private val amountRegex = Regex("(₹\\s?\\d[\\d,]*|\\b\\d[\\d,]*(?:\\.\\d+)?\\s?(?:rs|rupees?)\\b)", RegexOption.IGNORE_CASE)
        private val codeRegex = Regex("\\b\\d{4,8}\\b")
        private val whitespaceRegex = Regex("\\s+")
    }

    init {
        interpreter = Interpreter(loadModel(context))
        tokenizer = loadTokenizer(context)
        metadata = loadMetadata(context)
    }

    fun predict(text: String): ScamSignals {
        val normalized = normalizeText(text)
        if (normalized.length < 10) {
            return ScamSignals.safeFallback("Text too short for ML")
        }

        return try {
            runInference(normalized)
        } catch (e: Exception) {
            Log.e("ScamRiskModel", "ML failed: ${e.message}")
            ThreatLogger.logSystem("ML inference failed, falling back to rules")
            ScamSignals.safeFallback("ML inference failed")
        }
    }

    private fun runInference(normalizedText: String): ScamSignals {
        val input = tokenize(normalizedText)
        require(interpreter.outputTensorCount == EXPECTED_OUTPUT_TENSORS) {
            "Unexpected model outputs: ${interpreter.outputTensorCount}"
        }

        val outputMap = mutableMapOf<Int, Any>()
        for (i in 0 until interpreter.outputTensorCount) {
            val shape = interpreter.getOutputTensor(i).shape()
            outputMap[i] = Array(shape[0]) { FloatArray(shape[1]) }
        }

        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputMap)

        val scamScore = outputScalar(outputMap, OUTPUT_IS_SCAM)
        val isScam = scamScore > 0.55f

        val severityVec = outputAt(outputMap, OUTPUT_SEVERITY)
        val severity = severityVec?.argMax()?.plus(1) ?: 1

        val stageVec = outputAt(outputMap, OUTPUT_STAGE)
        val stage = ScamStage.values().getOrElse(stageVec?.argMax() ?: 0) { ScamStage.LURE }

        val actionVec = outputAt(outputMap, OUTPUT_ACTION)
        val action = ScamAction.values().getOrElse(actionVec?.argMax() ?: 0) { ScamAction.UNKNOWN }

        val scamTypeVec = outputAt(outputMap, OUTPUT_SCAM_TYPE)
        val scamType = metadata.scamTypeLabels.getOrNull(scamTypeVec?.argMax() ?: -1)

        val hasOtp = outputScalar(outputMap, OUTPUT_HAS_OTP) > 0.5f
        val hasUpi = outputScalar(outputMap, OUTPUT_HAS_UPI) > 0.5f
        val hasUrl = outputScalar(outputMap, OUTPUT_HAS_URL) > 0.5f
        val hasQr = outputScalar(outputMap, OUTPUT_HAS_QR) > 0.5f
        val hasPhone = outputScalar(outputMap, OUTPUT_HAS_PHONE) > 0.5f
        val hasThreat = outputScalar(outputMap, OUTPUT_HAS_THREAT) > 0.5f
        val hasUrgency = outputScalar(outputMap, OUTPUT_HAS_URGENCY) > 0.5f

        val confidence = listOfNotNull(
            severityVec?.maxOrNull(),
            stageVec?.maxOrNull(),
            actionVec?.maxOrNull(),
            scamTypeVec?.maxOrNull(),
            scamScore
        ).maxOrNull() ?: 0f

        return ScamSignals(
            isScam = isScam,
            severity = severity,
            scamType = scamType,
            scamStage = stage,
            requestedAction = action,
            hasOtp = hasOtp,
            hasUpi = hasUpi,
            hasUrl = hasUrl,
            hasQr = hasQr,
            hasPhone = hasPhone,
            hasThreat = hasThreat,
            hasUrgency = hasUrgency,
            confidence = confidence,
            explanation = buildExplanation(
                scamType = scamType,
                otp = hasOtp,
                upi = hasUpi,
                url = hasUrl,
                qr = hasQr,
                phone = hasPhone,
                threat = hasThreat,
                urgency = hasUrgency
            ),
            usedFallback = false,
            fallbackReason = null
        )
    }

    private fun tokenize(normalizedText: String): Array<IntArray> {
        val tokens = normalizedText.split(" ")
            .filter { it.isNotBlank() }
            .take(tokenizer.maxLen)
            .map { tokenizer.wordIndex[it] ?: tokenizer.oovIndex }

        val padded = IntArray(tokenizer.maxLen)
        tokens.forEachIndexed { index, value -> padded[index] = value }
        return arrayOf(padded)
    }

    private fun normalizeText(text: String): String {
        var normalized = text.lowercase()
        normalized = urlRegex.replace(normalized, " <URL> ")
        normalized = upiRegex.replace(normalized, " <UPI_ID> ")
        normalized = phoneRegex.replace(normalized, " <PHONE> ")
        normalized = amountRegex.replace(normalized, " <AMOUNT> ")
        normalized = codeRegex.replace(normalized, " <CODE> ")
        return whitespaceRegex.replace(normalized, " ").trim()
    }

    private fun FloatArray.argMax(): Int =
        indices.maxByOrNull { this[it] } ?: 0

    @Suppress("UNCHECKED_CAST")
    private fun outputAt(outputMap: Map<Int, Any>, index: Int): FloatArray? =
        (outputMap[index] as? Array<FloatArray>)?.getOrNull(0)

    private fun outputScalar(outputMap: Map<Int, Any>, index: Int): Float =
        outputAt(outputMap, index)?.getOrElse(0) { 0f } ?: 0f

    private fun buildExplanation(
        scamType: String?,
        otp: Boolean,
        upi: Boolean,
        url: Boolean,
        qr: Boolean,
        phone: Boolean,
        threat: Boolean,
        urgency: Boolean
    ): String =
        listOfNotNull(
            scamType?.let { "Pattern resembles $it" },
            "OTP mentioned".takeIf { otp },
            "Payment request detected".takeIf { upi },
            "Suspicious link detected".takeIf { url },
            "QR payment flow detected".takeIf { qr },
            "Callback number detected".takeIf { phone },
            "Threatening language detected".takeIf { threat },
            "Urgency pressure detected".takeIf { urgency }
        ).ifEmpty { listOf("No strong scam indicators detected") }.joinToString(", ")

    private fun loadModel(context: Context): ByteBuffer {
        val afd = context.assets.openFd("scam_signal.tflite")
        return afd.createInputStream().channel.map(
            FileChannel.MapMode.READ_ONLY,
            afd.startOffset,
            afd.declaredLength
        ).order(ByteOrder.nativeOrder())
    }

    private fun loadTokenizer(context: Context): TokenizerConfig {
        val json = context.assets.open("tokenizer.json")
            .bufferedReader().use { it.readText() }
        val root = JSONObject(json)

        if (root.has("word_index")) {
            val wordIndexJson = root.getJSONObject("word_index")
            val wordIndex = wordIndexJson.keys().asSequence()
                .associateWith { wordIndexJson.getInt(it) }
            return TokenizerConfig(
                wordIndex = wordIndex,
                maxLen = root.optInt("max_len", 64),
                oovIndex = root.optInt("oov_index", 1)
            )
        }

        val legacyWordIndexJson = JSONObject(
            root.getJSONObject("config").getString("word_index")
        )
        val legacyWordIndex = legacyWordIndexJson.keys().asSequence()
            .associateWith { legacyWordIndexJson.getInt(it) }
        return TokenizerConfig(
            wordIndex = legacyWordIndex,
            maxLen = 40,
            oovIndex = 1
        )
    }

    private fun loadMetadata(context: Context): ModelMetadata {
        val json = context.assets.open("model_metadata.json")
            .bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val scamTypes = mutableListOf<String>()
        val labels = root.optJSONArray("scam_type_labels") ?: JSONArray()
        for (i in 0 until labels.length()) {
            scamTypes.add(labels.getString(i))
        }
        return ModelMetadata(scamTypeLabels = scamTypes)
    }

    private data class TokenizerConfig(
        val wordIndex: Map<String, Int>,
        val maxLen: Int,
        val oovIndex: Int
    )

    private data class ModelMetadata(
        val scamTypeLabels: List<String>
    )
}
