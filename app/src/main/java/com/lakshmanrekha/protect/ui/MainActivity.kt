package com.lakshmanrekha.protect.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import com.lakshmanrekha.protect.theme.LakshmanRekhaTheme
import com.lakshmanrekha.protect.utils.AppState
import com.lakshmanrekha.protect.utils.StartupNotifier

class MainActivity : ComponentActivity() {

    // 🔔 Notification permission (Android 13+)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                StartupNotifier.showProtectionStarted(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // 🔁 Single source of onboarding truth
            var step by rememberSaveable {
                mutableStateOf(
                    when {
                        AppState.language == null -> OnboardingStep.LANGUAGE
                        !AppState.isSetupComplete -> OnboardingStep.PROFILE
                        else -> OnboardingStep.DONE
                    }
                )
            }

            LakshmanRekhaTheme {

                when (step) {

                    // 🌐 Language
                    OnboardingStep.LANGUAGE -> {
                        LanguageSelectionScreen { selected ->
                            AppState.language = selected
                            step = OnboardingStep.PROFILE
                        }
                    }

                    // 👤 Profile
                    OnboardingStep.PROFILE -> {
                        ProfileSetupScreen { name, age, mode ->
                            AppState.name = name
                            AppState.age = age
                            AppState.protectionMode = mode
                            AppState.isSetupComplete = true

                            requestNotificationPermissionIfNeeded()
                            step = OnboardingStep.DONE
                        }
                    }

                    // 🛡️ Console
                    OnboardingStep.DONE -> {
                        HomeConsoleScreen()
                    }
                }
            }
        }
    }

    // 🔐 Permission handling
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
                StartupNotifier.showProtectionStarted(this)
            }
        } else {
            StartupNotifier.showProtectionStarted(this)
        }
    }
}