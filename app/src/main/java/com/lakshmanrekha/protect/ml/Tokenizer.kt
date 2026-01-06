package com.lakshmanrekha.protect.ml

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

class Tokenizer(context: Context) {

    private val wordIndex: Map<String, Int>
    private val maxLen = 128

    init {
        val json = loadJSON(context)
        val obj = JSONObject(json)
        val indexObj = obj.getJSONObject("word_index")

        val map = mutableMapOf<String, Int>()
        indexObj.keys().forEach {
            map[it] = indexObj.getInt(it)
        }
        wordIndex = map
    }

    fun tokenize(text: String): IntArray {
        val tokens = IntArray(maxLen)
        val words = text.lowercase()
            .replace("[^a-z0-9 ]".toRegex(), "")
            .split(" ")

        for (i in words.indices.take(maxLen)) {
            tokens[i] = wordIndex[words[i]] ?: 0
        }
        return tokens
    }

    private fun loadJSON(context: Context): String {
        val input: InputStream = context.assets.open("tokenizer.json")
        return input.bufferedReader().use { it.readText() }
    }
}