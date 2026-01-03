package com.lakshmanrekha.protect.utils

object AppState {

    // Senior profile
    var name: String = ""
    var age: Int = 0

    // Language chosen during onboarding
    var language: AppLanguage? = null

    // Selected protection mode
    var protectionMode: ProtectionMode = ProtectionMode.NONE

    // Has onboarding finished?
    var isSetupComplete: Boolean = false

    // Reason if protection was downgraded automatically
    var lastDowngradeReason: String? = null
    var shouldStartService: Boolean = false

    var statusServiceStarted: Boolean = false

}