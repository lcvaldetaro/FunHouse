# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# --- App-Specific Models ---
-keep class com.gepetto.funhouse.** { *; }
-keep class com.funhouse.shared.common.models.** { *; }
-keep class models.** { *; }
-keep class jni.** { *; }

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses, Signature
-dontwarn kotlinx.serialization.**

# --- Custom Libraries (Gepetto) ---
-keep class club.gepetto.** { *; }

# --- Networking (Retrofit, OkHttp) ---
# Retrofit
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-keepattributes Signature
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# --- Third Party Libraries ---
# Coil
-dontwarn coil.**

# Accompanist
-dontwarn com.google.accompanist.**

# BouncyCastle / Conscrypt / OpenJSSE
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# --- General ---
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
