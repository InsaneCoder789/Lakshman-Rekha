# Remove logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Keep core logic
-keep class com.lakshmanrekha.protect.core.** { *; }
-keep class com.lakshmanrekha.protect.services.** { *; }

# Obfuscate everything else
-dontwarn **