package com.lakshmanrekha.protect.ml

/**
 * Stage of scam progression (dataset-aligned)
 *
 * This represents *where* in the scam lifecycle the message or call is.
 * It is inferred by the ML model but used by the app to decide escalation.
 */
enum class ScamStage {

    /**
     * Initial bait or hook.
     * Example: prize won, delivery issue, job offer, warning message.
     */
    LURE,

    /**
     * Active attempt to make the user act.
     * Example: click link, share OTP, install app, send money.
     */
    ACTION,

    /**
     * Coercion or fear-based escalation.
     * Example: arrest threat, account freeze, power cut, FIR warning.
     */
    THREAT
}