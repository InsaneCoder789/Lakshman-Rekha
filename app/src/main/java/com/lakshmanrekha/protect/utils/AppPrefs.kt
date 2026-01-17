package com.lakshmanrekha.protect.utils

import android.content.Context

object AppPrefs {

    private const val PREFS = "lakshman_rekha_prefs"

    // Profile
    private const val KEY_NAME = "name"
    private const val KEY_AGE = "age"
    private const val KEY_LANG = "lang"
    private const val KEY_MODE = "mode"

    // Onboarding
    private const val KEY_SEEN_WELCOME = "seen_welcome"
    private const val KEY_SEEN_EXPLANATION = "seen_explanation"
    private const val KEY_DONE = "done"

    // Trusted contacts
    private const val TRUSTED = "trusted_contacts"
    private const val TRUSTED_DONE = "trusted_done"

    // 🆕 Family alerts (OPT-IN)
    private const val KEY_FAMILY_ALERTS = "family_alerts"
    private const val PREF_INSTALL_VERSION = "install_version"
    private const val CURRENT_INSTALL_VERSION = 1

    /* =========================================================
     * SAVE
     * ========================================================= */

    fun save(context: Context) {

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAME, AppState.name)
            .putInt(KEY_AGE, AppState.age)
            .putString(KEY_LANG, AppState.language?.name)
            .putString(KEY_MODE, AppState.protectionMode.name)
            .putBoolean(KEY_SEEN_WELCOME, AppState.hasSeenWelcome)
            .putBoolean(KEY_SEEN_EXPLANATION, AppState.hasSeenModeExplanation)
            .putBoolean(KEY_DONE, AppState.isSetupComplete)
            .putBoolean(KEY_FAMILY_ALERTS, AppState.familyAlertsEnabled)
            .apply()
    }

    /* =========================================================
     * LOAD
     * ========================================================= */

    fun load(context: Context) {

        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Onboarding state
        AppState.hasSeenWelcome =
            p.getBoolean(KEY_SEEN_WELCOME, false)

        AppState.hasSeenModeExplanation =
            p.getBoolean(KEY_SEEN_EXPLANATION, false)

        AppState.isSetupComplete =
            p.getBoolean(KEY_DONE, false)

        // Language
        p.getString(KEY_LANG, null)?.let {
            try {
                AppState.language = AppLanguage.valueOf(it)
            } catch (_: Exception) {}
        }

        // Profile
        AppState.name = p.getString(KEY_NAME, "") ?: ""
        AppState.age = p.getInt(KEY_AGE, 0)

        // Mode
        val modeString =
            p.getString(KEY_MODE, ProtectionMode.SAATHI.name)

        AppState.protectionMode = try {
            ProtectionMode.valueOf(modeString!!)
        } catch (_: Exception) {
            ProtectionMode.SAATHI
        }

        // Trusted contacts
        AppState.trustedContacts =
            p.getStringSet(TRUSTED, emptySet())
                ?.toMutableSet() ?: mutableSetOf()

        AppState.hasAddedTrustedContacts =
            p.getBoolean(TRUSTED_DONE, false)

        // 🆕 Family alert opt-in
        AppState.familyAlertsEnabled =
            p.getBoolean(KEY_FAMILY_ALERTS, false)
    }

    /* =========================================================
     * TRUSTED CONTACT HELPERS
     * ========================================================= */

    fun getTrustedContacts(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(TRUSTED, emptySet()) ?: emptySet()

    fun saveTrustedContacts(context: Context, set: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(TRUSTED, set)
            .putBoolean(TRUSTED_DONE, true)
            .apply()
    }

    /* =========================================================
     * MODE UPDATE
     * ========================================================= */

    fun updateMode(context: Context, mode: ProtectionMode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, mode.name)
            .apply()
    }
}