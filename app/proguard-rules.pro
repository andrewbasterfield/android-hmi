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

# --- CRITICAL FIX FOR HIVEMQ / NETTY / JCTOOLS ---
# These libraries use AtomicFieldUpdaters which access fields by string name.
# R8 must not rename or strip these fields.

# 1. Protect the specific field names used in concurrent queues
-keepclassmembers class * {
    volatile long producerIndex;
    volatile long consumerIndex;
    volatile int producerIndex;
    volatile int consumerIndex;
}

# 2. Broader protection for HiveMQ internals
-dontwarn com.hivemq.client.**
-keep class com.hivemq.client.** { *; }
-keep class com.hivemq.client.internal.** {
    volatile <fields>;
}

# 3. Broader protection for Netty internals
-dontwarn io.netty.**
-keep class io.netty.** { *; }
-keepclassmembers class io.netty.util.internal.** {
    volatile <fields>;
}

# 4. JCTools (often shaded or used internally by HiveMQ/Netty)
-dontwarn org.jctools.**
-keep class org.jctools.** { *; }
-keepclassmembers class org.jctools.** {
    volatile <fields>;
}

# --- END CRITICAL FIX ---
