package com.lakshmanrekha.protect.utils

/**
 * Protection modes in increasing order of strictness.
 * Order is IMPORTANT — do not change without updating logic.
 */
enum class ProtectionMode(val level: Int) {

    NONE(0),        // Before setup / protection off

    SAATHI(1),      // Passive monitoring, logging, gentle warnings

    LAKSHMAN(2),    // Active warnings, coaching, blocking suggestions

    RAKSHA(3);      // Strong intervention (overlays, emergency actions)

    fun isAtLeast(other: ProtectionMode): Boolean {
        return this.level >= other.level
    }
}