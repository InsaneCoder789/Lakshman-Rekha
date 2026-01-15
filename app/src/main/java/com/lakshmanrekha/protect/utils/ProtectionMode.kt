package com.lakshmanrekha.protect.utils

/**
 * Protection modes in increasing order of strictness.
 * ORDER IS CRITICAL — used by escalation logic.
 */
enum class ProtectionMode(val level: Int) {

    NONE(0),        // Protection disabled / before setup

    SAATHI(1),      // Advisory only

    RAKSHA(2),      // Guided protection (warnings, coaching)

    LAKSHMAN(3);    // Strong autonomous protection (overlays, SOS)

    fun isAtLeast(other: ProtectionMode): Boolean {
        return this.level >= other.level
    }
}