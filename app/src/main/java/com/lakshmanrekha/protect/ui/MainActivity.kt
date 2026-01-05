package com.lakshmanrekha.protect.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.core.ProtectionNotifier
import com.lakshmanrekha.protect.core.SOSController
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme
import com.lakshmanrekha.protect.utils.*

class MainActivity : ComponentActivity() {

    // 🔔 Android 13+ notification permission
    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) safeShowNotification()
        }
    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                ThreatLogger.logSystem("SMS permission denied by user")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Load persisted state
        AppPrefs.load(this)

        setContent {

            // 2️⃣ Single source of navigation truth
            var step by remember { mutableStateOf(calculateStep()) }

            LakshmanRekhaTheme {

                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        fadeIn() + slideInHorizontally { it } togetherWith
                                fadeOut() + slideOutHorizontally { -it }
                    },
                    label = "ScreenTransition"
                ) { currentStep ->

                    when (currentStep) {

                        OnboardingStep.LANGUAGE -> {
                            LanguageSelectionScreen { language ->
                                AppState.language = language
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }
                        }

                        OnboardingStep.WELCOME -> {
                            WelcomeScreen {
                                AppState.hasSeenWelcome = true
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }
                        }

                        OnboardingStep.MODE_EXPLANATION -> {
                            ModeExplanationScreen {
                                AppState.hasSeenModeExplanation = true
                                AppPrefs.save(this@MainActivity)
                                step = calculateStep()
                            }
                        }

                        OnboardingStep.PROFILE -> {
                            ProfileSetupScreen { name, age, mode ->
                                AppState.apply {
                                    this.name = name
                                    this.age = age
                                    this.protectionMode = mode
                                    this.isSetupComplete = true
                                }
                                AppPrefs.save(this@MainActivity)
                                requestNotificationPermissionIfNeeded()
                                step = calculateStep()
                            }
                        }

                        OnboardingStep.TRUSTED_CONTACTS -> {
                            TrustedContactsScreen {
                                step = calculateStep()

                                if (ContextCompat.checkSelfPermission(
                                        this@MainActivity,
                                        Manifest.permission.SEND_SMS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                                }
                            }
                        }

                        OnboardingStep.DONE -> {
                            HomeConsoleScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        safeShowNotification()
    }

    /**
     * 🚨 SOS TRIGGER
     * Volume Up / Down pressed 3 times → SOSController
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                SOSController.onVolumePressed(this)
                return true // consume event
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /* ---------------------------------------------------
     * HELPERS
     * --------------------------------------------------- */

    private fun calculateStep(): OnboardingStep =
        when {
            AppState.language == null -> OnboardingStep.LANGUAGE
            !AppState.hasSeenWelcome -> OnboardingStep.WELCOME
            !AppState.hasSeenModeExplanation -> OnboardingStep.MODE_EXPLANATION
            !AppState.isSetupComplete -> OnboardingStep.PROFILE
            !AppState.hasAddedTrustedContacts -> OnboardingStep.TRUSTED_CONTACTS
            else -> OnboardingStep.DONE
        }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
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

    private fun safeShowNotification() {
        if (!AppState.isSetupComplete || AppState.protectionMode == ProtectionMode.NONE) return
        try {
            ProtectionNotifier.show(this, AppState.protectionMode)
        } catch (_: Exception) {
            // Never crash UI for notification issues
        }
    }
}