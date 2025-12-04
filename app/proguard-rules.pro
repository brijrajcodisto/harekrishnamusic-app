# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# project.ext.proguardFiles and project.ext.testProguardFiles properties

-keepattributes *Annotation*
-keep class * {
    public protected *;
}
-keep interface * {
    public protected *;
}

# Retrofit rules
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson rules
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Room rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
