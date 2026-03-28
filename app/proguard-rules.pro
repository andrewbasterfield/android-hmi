# Missing classes from transitive dependencies
-dontwarn org.joda.convert.**
-dontwarn org.slf4j.**
-dontwarn reactor.blockhound.**

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.example.hmi.data.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Netty (for MQTT)
-dontwarn io.netty.**
-keep class io.netty.** { *; }

# Keep all data classes for Gson serialization (preserve field names)
-keep class com.example.hmi.data.** { *; }
-keepclassmembers class com.example.hmi.data.** {
    <fields>;
    <init>(...);
}

# Prevent R8 from stripping default values
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
