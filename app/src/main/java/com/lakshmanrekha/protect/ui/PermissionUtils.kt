package com.lakshmanrekha.protect.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

object PermissionUtils {

    // 1. Notification Listener Check
    fun isNotificationAccessEnabled(context: Context): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(packageName)
    }

    // 2. Accessibility Check (Updated to use Manager API for instant detection)
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)

        // This looks for ANY service belonging to your app that is currently active
        return enabledServices.any { it.resolveInfo.serviceInfo.packageName == context.packageName }
    }

    fun hasAllRequiredPermissions(context: Context): Boolean {
        return isNotificationAccessEnabled(context) && isAccessibilityServiceEnabled(context)
    }
}