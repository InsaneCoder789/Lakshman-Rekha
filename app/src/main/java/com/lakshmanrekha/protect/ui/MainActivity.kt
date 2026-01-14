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

    // --- MISSING LOGIC RESTORED: PERMISSION LAUNCHERS ---

    // 🔔 Android 13+ notification permission (Restored from old)
    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) safeShowNotification()
        }

    // 📩 SMS permission for SOS (Restored from old)
    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                ThreatLogger.logSystem("SMS permission denied - SOS functionality limited")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- MISSING LOGIC RESTORED: ML INITIALIZATION ---
        if (RuntimeState.scamRiskModel == null) {
            RuntimeState.scamRiskModel = ScamRiskModel(this)
        }

        AppPrefs.load(this)

        setContent {
            var step by rememberSaveable {
                mutableStateOf(calculateStep())
            }

            LakshmanRekhaTheme {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "Onboarding"
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

                                // Trigger permission request after profile setup
                                requestNotificationPermissionIfNeeded()

                                step = calculateStep()
                            }

                        OnboardingStep.PERMISSIONS ->
                            PermissionScreen {
                                step = calculateStep()
                            }

                        OnboardingStep.TRUSTED_CONTACTS ->
                            TrustedContactsScreen {
                                // Restoration: Trigger SMS request after contacts are added
                                checkAndRequestSmsPermission()
                                step = calculateStep()
                            }

                        OnboardingStep.DONE ->
                            HomeConsoleScreen()
                    }
                }
            }
        }
    }

    // --- MISSING LOGIC RESTORED: LIFECYCLE & SOS ---

    override fun onResume() {
        super.onResume()
        safeShowNotification()
    }

    /**
     * 🚨 SOS Trigger: Hardware Volume Buttons
     * Restored from old version for OEM-safe event handling
     */
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

    /* ---------------------------------------------------
     * HELPERS (Combined Logic)
     * --------------------------------------------------- */

    private fun calculateStep(): OnboardingStep =
        when {
            AppState.language == null -> OnboardingStep.LANGUAGE
            !AppState.hasSeenWelcome -> OnboardingStep.WELCOME
            !AppState.hasSeenModeExplanation -> OnboardingStep.MODE_EXPLANATION
            !AppState.isSetupComplete -> OnboardingStep.PROFILE
            !PermissionUtils.isNotificationAccessEnabled(this@MainActivity) ||
                    !PermissionUtils.isAccessibilityServiceEnabled(this@MainActivity) -> OnboardingStep.PERMISSIONS
            !AppState.hasAddedTrustedContacts -> OnboardingStep.TRUSTED_CONTACTS
            else -> OnboardingStep.DONE
        }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                safeShowNotification()
            }
        } else {
            safeShowNotification()
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    private fun safeShowNotification() {
        if (!AppState.isSetupComplete) return
        if (AppState.protectionMode == ProtectionMode.NONE) return

        try {
            ProtectionNotifier.show(this, AppState.protectionMode)
        } catch (_: Exception) {
            // Never crash UI for non-critical notification failures
        }
    }
}