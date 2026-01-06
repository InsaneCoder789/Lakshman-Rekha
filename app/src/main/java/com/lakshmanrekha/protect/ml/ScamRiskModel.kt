package com.lakshmanrekha.protect.ml

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ScamRiskModel(context: Context) {

    private val interpreter: Interpreter
    private val wordIndex: Map<String, Int>
    private val maxLen = 40

    init {
        interpreter = Interpreter(loadModel(context))
        wordIndex = loadTokenizer(context)
    }

    /* ================== PUBLIC API ================== */

    fun predict(text: String): ScamPrediction {

        val input = tokenize(text)
        val output = Array(1) { FloatArray(3) }

        interpreter.run(input, output)

        val probs = output[0]
        val maxIdx = probs.indices.maxByOrNull { probs[it] } ?: 0

        val label = when (maxIdx) {
            0 -> ScamLabel.SAFE
            1 -> ScamLabel.SUSPICIOUS
            else -> ScamLabel.LIKELY_SCAM
        }

        val explanation = when (label) {
            ScamLabel.SAFE ->
                "No scam patterns detected in this message."

            ScamLabel.SUSPICIOUS ->
                "This message shows patterns often used in scams. Please be cautious."

            ScamLabel.LIKELY_SCAM ->
                "High risk scam detected. Do not share OTP, money, or personal details."
        }

        return ScamPrediction(
            label = label,
            confidence = probs[maxIdx],
            explanation = explanation
        )
    }

    /* ================== TOKENIZATION ================== */

    private fun tokenize(text: String): Array<IntArray> {

        val tokens = text
            .lowercase()
            .split(Regex("\\s+"))
            .mapNotNull { wordIndex[it] }
            .takeLast(maxLen)

        val padded = IntArray(maxLen)
        val start = maxLen - tokens.size

        tokens.forEachIndexed { index, value ->
            padded[start + index] = value
        }

        return arrayOf(padded)
    }

    /* ================== LOADERS ================== */

    private fun loadModel(context: Context): ByteBuffer {
        val afd = context.assets.openFd("scam_model.tflite")
        val input = afd.createInputStream()
        val channel = input.channel

        val buffer = channel.map(
            FileChannel.MapMode.READ_ONLY,
            afd.startOffset,
            afd.declaredLength
        )

        return buffer.order(ByteOrder.nativeOrder())
    }

    private fun loadTokenizer(context: Context): Map<String, Int> {

        val json = context.assets
            .open("tokenizer.json")
            .bufferedReader()
            .use { it.readText() }

        val obj = JSONObject(json)
        val wordMap = obj
            .getJSONObject("config")
            .getJSONObject("word_index")

        val map = mutableMapOf<String, Int>()
        wordMap.keys().forEach { key ->
            map[key] = wordMap.getInt(key)
        }

        return map
    }
}