package com.lakshmanrekha.protect.utils

import android.content.Context

object AppPrefs {

    private const val PREFS = "lakshman_rekha_prefs"
    private const val TRUSTED = "trusted_contacts"

    // Keys for better maintenance
    private const val KEY_NAME = "name"
    private const val KEY_AGE = "age"
    private const val KEY_LANG = "lang"
    private const val KEY_MODE = "mode"
    private const val KEY_SEEN_WELCOME = "seen_welcome"
    private const val KEY_SEEN_EXPLANATION = "seen_explanation"
    private const val KEY_DONE = "done"
    private const val TRUSTED_DONE = "trusted_done"

    fun save(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_NAME, AppState.name)
            .putInt(KEY_AGE, AppState.age)
            .putString(KEY_LANG, AppState.language?.name)
            .putString(KEY_MODE, AppState.protectionMode.name)
            .putBoolean(KEY_SEEN_WELCOME, AppState.hasSeenWelcome)
            .putBoolean(KEY_SEEN_EXPLANATION, AppState.hasSeenModeExplanation)
            .putBoolean(KEY_DONE, AppState.isSetupComplete)
            .apply()
    }

    fun load(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // 1. Load intermediate steps first (CRITICAL FIX)
        // This ensures the app remembers where you left off in onboarding
        AppState.hasSeenWelcome = p.getBoolean(KEY_SEEN_WELCOME, false)
        AppState.hasSeenModeExplanation = p.getBoolean(KEY_SEEN_EXPLANATION, false)
        AppState.isSetupComplete = p.getBoolean(KEY_DONE, false)

        // Load Language safely
        p.getString(KEY_LANG, null)?.let {
            try { AppState.language = AppLanguage.valueOf(it) } catch(e: Exception) {}
        }

        // 2. Load the rest of the profile data
        AppState.name = p.getString(KEY_NAME, "") ?: ""
        AppState.age = p.getInt(KEY_AGE, 0)

        val modeString = p.getString(KEY_MODE, ProtectionMode.SAATHI.name)
        try {
            AppState.protectionMode = ProtectionMode.valueOf(modeString!!)
        } catch (e: Exception) {
            AppState.protectionMode = ProtectionMode.SAATHI
        }

        AppState.trustedContacts =
            p.getStringSet(TRUSTED, emptySet())?.toMutableSet() ?: mutableSetOf()
        AppState.hasAddedTrustedContacts =
            p.getBoolean(TRUSTED_DONE, false)
    }

    fun getTrustedContacts(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(TRUSTED, emptySet()) ?: emptySet()

    fun saveTrustedContacts(context: Context, set: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(TRUSTED, set)
            .putBoolean(TRUSTED_DONE, true) // ✅ MARK COMPLETED
            .apply()
    }

    fun updateMode(context: Context, mode: ProtectionMode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, mode.name)
            .apply()
    }
}