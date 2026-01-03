package com.lakshmanrekha.protect.utils

object LanguageManager {

    fun isHindi(): Boolean =
        AppState.language == AppLanguage.HINDI

    fun isEnglish(): Boolean =
        AppState.language == AppLanguage.ENGLISH
}