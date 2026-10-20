# R8 rules for the ad SDKs used by this app.
# Unity LevelPlay Android SDK: https://docs.unity.com/grow/levelplay/sdk/android/sdk-integration
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keep class com.ironsource.adapters.** { *; }
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-dontwarn com.ironsource.**
-keep class com.iab.omid.** { *; }
-dontwarn com.iab.omid.**

# Meta Audience Network adapter: https://docs.unity.com/grow/levelplay/sdk/android/networks/guides/meta-audience-network
-dontwarn com.facebook.ads.internal.**
-keeppackagenames com.facebook.*
-keep public class com.facebook.ads.** { public protected *; }

# Unity Ads adapter: https://docs.unity.com/grow/levelplay/sdk/unity/networks/guides/unity-ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-dontwarn com.google.ar.core.**
-dontwarn com.unity3d.services.**
-dontwarn com.ironsource.adapters.unityads.**

# Firebase Crashlytics: retain readable source locations in uploaded R8 mappings.
-keepattributes SourceFile,LineNumberTable