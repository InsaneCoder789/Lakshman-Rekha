package com.lakshmanrekha.protect.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.core.ProtectionNotifier
import com.lakshmanrekha.protect.core.SOSController
import com.lakshmanrekha.protect.ml.ScamRiskModel
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme
import com.lakshmanrekha.protect.utils.*

class MainActivity : ComponentActivity() {

    /* =========================================================
     * PERMISSION LAUNCHERS
     * ========================================================= */

    // Android 13+ notification permission
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) safeShowNotification()
        }

    // SMS and Call Log permissions for SOS and Call Tracking
    private val safetyPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val smsGranted = results[Manifest.permission.SEND_SMS] ?: false
            val callLogGranted = results[Manifest.permission.READ_CALL_LOG] ?: false
            
            if (!smsGranted) {
                ThreatLogger.logSystem("SMS permission denied – SOS limited")
            }
            if (!callLogGranted) {
                ThreatLogger.logSystem("Call Log permission denied – Trusted check disabled")
            }
        }

    /* =========================================================
     * LIFECYCLE
     * ========================================================= */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔒 Initialize ML once
        if (RuntimeState.scamRiskModel == null) {
            RuntimeState.scamRiskModel = ScamRiskModel(this)
        }

        // 🔒 Load persisted app state
        AppPrefs.load(this)

        // 🔒 HARD RESET runtime state (CRITICAL for Android 15/16 reinstalls)
        RuntimeState.resetSession()

        setContent {
            var step by rememberSaveable {
                mutableStateOf(calculateStep())
            }

            LakshmanRekhaTheme {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "OnboardingFlow"
                ) { currentStep ->
                    when (currentStep) {

                        OnboardingStep.LANGUAGE ->
                            LanguageSelectionScreen {
                                AppState.language = it
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }

                        OnboardingStep.WELCOME ->
                            WelcomeScreen {
                                AppState.hasSeenWelcome = true
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }

                        OnboardingStep.MODE_EXPLANATION ->
                            ModeExplanationScreen {
                                AppState.hasSeenWelcome = true // Ensure welcome is marked
                                AppState.hasSeenModeExplanation = true
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }

                        OnboardingStep.PROFILE ->
                            ProfileSetupScreen { name, age, mode ->
                                AppState.name = name
                                AppState.age = age
                                AppState.protectionMode = mode
                                AppState.isSetupComplete = true
                                AppPrefs.save(this@MainActivity)

                                requestNotificationPermissionIfNeeded()
                                step = calculateStep()
                            }

                        OnboardingStep.PERMISSIONS ->
                            PermissionScreen {
                                step = calculateStep()
                            }

                        OnboardingStep.TRUSTED_CONTACTS ->
                            TrustedContactsScreen {
                                checkAndRequestSafetyPermissions()
                                step = calculateStep()
                            }

                        OnboardingStep.DONE ->
                            HomeConsoleScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        RuntimeState.appInForeground = true
        safeShowNotification()
    }

    override fun onPause() {
        super.onPause()
        RuntimeState.appInForeground = false
    }

    /* =========================================================
     * SOS – HARDWARE BUTTON TRIGGER
     * ========================================================= */

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    SOSController.onVolumePressed(this)
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /* =========================================================
     * FLOW CONTROL
     * ========================================================= */

    private fun calculateStep(): OnboardingStep =
        when {
            AppState.language == null ->
                OnboardingStep.LANGUAGE

            !AppState.hasSeenWelcome ->
                OnboardingStep.WELCOME

            !AppState.hasSeenModeExplanation ->
                OnboardingStep.MODE_EXPLANATION

            !AppState.isSetupComplete ->
                OnboardingStep.PROFILE

            !PermissionUtils.isNotificationAccessEnabled(this) ||
                    !PermissionUtils.isAccessibilityServiceEnabled(this) ->
                OnboardingStep.PERMISSIONS

            !AppState.hasAddedTrustedContacts ->
                OnboardingStep.TRUSTED_CONTACTS

            else ->
                OnboardingStep.DONE
        }

    /* =========================================================
     * PERMISSIONS
     * ========================================================= */

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                safeShowNotification()
            }
        } else {
            safeShowNotification()
        }
    }

    private fun checkAndRequestSafetyPermissions() {
        val permissions = mutableListOf(Manifest.permission.SEND_SMS)
        
        // READ_CALL_LOG is required for incoming number detection on API 28+
        permissions.add(Manifest.permission.READ_CALL_LOG)

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            safetyPermissionsLauncher.launch(missing.toTypedArray())
        }
    }

    /* =========================================================
     * STATUS NOTIFICATION
     * ========================================================= */

    private fun safeShowNotification() {
        if (!AppState.isSetupComplete) return
        if (AppState.protectionMode == ProtectionMode.NONE) return

        try {
            ProtectionNotifier.show(this, AppState.protectionMode)
        } catch (_: Exception) {
            // Never crash UI for notification issues
        }
    }
}