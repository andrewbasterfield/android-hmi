# Missing classes from transitive dependencies
-dontwarn org.joda.convert.**
-dontwarn org.slf4j.**
-dontwarn reactor.blockhound.**

# Compose
-dontwarn androidx.compose.**

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.hmi.data.**$$serializer { *; }
-keepclassmembers class com.example.hmi.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.hmi.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.hmi.protocol.**$$serializer { *; }
-keepclassmembers class com.example.hmi.protocol.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.hmi.protocol.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Netty (for MQTT)
-dontwarn io.netty.**
-keep class io.netty.** { *; }
