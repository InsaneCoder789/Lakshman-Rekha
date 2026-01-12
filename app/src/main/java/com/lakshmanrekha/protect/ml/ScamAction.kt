package com.lakshmanrekha.protect.ml

/**
 * What the scammer is trying to make the user do
 *
 * ORDER MUST MATCH ML TRAINING OUTPUT
 * UNKNOWN is a fallback and is NOT predicted by the model
 */
enum class ScamAction {
    CALL_NUMBER,     // 0
    CLICK_LINK,      // 1
    PAY_UPI,         // 2
    SHARE_DETAILS,   // 3
    SEND_OTP,        // 4
    INSTALL_APP,     // 5
    UNKNOWN          // 6
}