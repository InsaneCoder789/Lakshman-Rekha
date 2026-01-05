package com.lakshmanrekha.protect.utils

object Strings {

    // ---------- GENERAL ----------

    fun greeting(name: String): String =
        if (LanguageManager.isHindi())
            "नमस्ते ${name.ifBlank { "" }} 👋"
        else
            "Hello ${name.ifBlank { "there" }} 👋"

    fun recentActivity(): String =
        if (LanguageManager.isHindi())
            "हाल की गतिविधि"
        else
            "Recent Activity"

    fun systemStatus(): String =
        if (LanguageManager.isHindi())
            "सिस्टम स्थिति"
        else
            "System Status"

    // ---------- PROTECTION ----------

    fun protectionStatus(): String =
        if (LanguageManager.isHindi()) {
            when (AppState.protectionMode) {
                ProtectionMode.RAKSHA -> "🟢 रक्षा मोड सक्रिय है"
                ProtectionMode.LAKSHMAN -> "🟡 लक्ष्मण मोड सक्रिय है"
                ProtectionMode.SAATHI -> "🟢 साथी मोड सक्रिय है"
                ProtectionMode.NONE -> "🔴 सुरक्षा बंद है"
            }
        } else {
            when (AppState.protectionMode) {
                ProtectionMode.RAKSHA -> "🟢 RAKSHA Mode Active"
                ProtectionMode.LAKSHMAN -> "🟡 LAKSHMAN Mode Active"
                ProtectionMode.SAATHI -> "🟢 SAATHI Mode Active"
                ProtectionMode.NONE -> "🔴 Protection Inactive"
            }
        }

    fun changeProtectionMode(): String =
        if (LanguageManager.isHindi())
            "सुरक्षा मोड बदलें"
        else
            "Change Protection Mode"

    fun noThreats(): String =
        if (LanguageManager.isHindi())
            "अब तक कोई खतरा नहीं मिला।\nलक्ष्मण रेखा आपकी रक्षा कर रही है।"
        else
            "No threats detected yet.\nLakshman Rekha is protecting you."

    // ---------- COACH ----------

    fun otpWarning(): String =
        if (LanguageManager.isHindi())
            "कोई भी बैंक या ऐप कभी OTP नहीं मांगता।"
        else
            "No bank or app will ever ask for your OTP."

    fun paymentWarning(): String =
        if (LanguageManager.isHindi())
            "कॉल के दौरान भुगतान करना खतरनाक हो सकता है।"
        else
            "Making payments during a call can be dangerous."

    fun coachTitle(): String =
        if (LanguageManager.isHindi())
            "कृपया रुकें"
        else
            "Please Pause"

    fun understood(): String =
        if (LanguageManager.isHindi())
            "मैं समझ गया / गई"
        else
            "I Understand"
}