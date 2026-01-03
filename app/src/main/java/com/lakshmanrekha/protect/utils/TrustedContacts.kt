package com.lakshmanrekha.protect.utils

object TrustedContacts {

    // Later: persist securely (DataStore / encrypted prefs)
    private val trustedNumbers = mutableSetOf<String>()

    fun add(number: String) {
        trustedNumbers.add(number)
    }

    fun isTrusted(number: String?): Boolean {
        if (number.isNullOrBlank()) return false
        return trustedNumbers.any { number.endsWith(it.takeLast(6)) }
    }
}