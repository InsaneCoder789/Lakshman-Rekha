package com.lakshmanrekha.protect.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import com.lakshmanrekha.protect.utils.ProtectionMode

@Composable
fun OnboardingHost(
    step: OnboardingStep,
    onLanguageDone: () -> Unit,
    onWelcomeDone: () -> Unit,
    onModeExplanationDone: () -> Unit,
    onProfileDone: (name: String, age: Int, mode: ProtectionMode) -> Unit,
    onPermissionsDone: () -> Unit,
    onContactsDone: () -> Unit
) {

    // ✅ ONE surface for ENTIRE onboarding
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally { it / 3 } togetherWith
                        fadeOut(animationSpec = tween(200)) +
                        slideOutHorizontally { -it / 3 }
            },
            label = "OnboardingFlow"
        ) { currentStep ->

            when (currentStep) {

                OnboardingStep.LANGUAGE ->
                    LanguageSelectionScreen(
                        onLanguageSelected = { onLanguageDone() }
                    )

                OnboardingStep.WELCOME ->
                    WelcomeScreen(
                        onContinue = onWelcomeDone
                    )

                OnboardingStep.MODE_EXPLANATION ->
                    ModeExplanationScreen(
                        onContinue = onModeExplanationDone
                    )

                OnboardingStep.PROFILE ->
                    ProfileSetupScreen(
                        onComplete = onProfileDone
                    )

                OnboardingStep.PERMISSIONS ->
                    PermissionScreen(
                        onContinue = onPermissionsDone
                    )

                OnboardingStep.TRUSTED_CONTACTS ->
                    TrustedContactsScreen(
                        onContinue = onContactsDone
                    )

                OnboardingStep.DONE -> {
                    // ⚠️ This should never render here
                    // MainActivity will switch to HomeConsoleScreen
                }
            }
        }
    }
}