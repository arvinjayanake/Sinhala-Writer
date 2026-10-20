# Sinhala Writer (Android)

This Android Jetpack Compose app ports the Flutter project's Android behavior. It keeps the
same package name (`com.arvin.sinhalawriter`), version (`2.0.8` / `18`), transliteration
tables and replacement order, writer actions, six Help sections, About content, theme,
artwork, Firebase Analytics and Crashlytics configuration, and Unity LevelPlay ads.

The English draft is saved on each edit. On first launch after upgrading the same Android
app, it also reads Flutter's `FlutterSharedPreferences` / `flutter.data` draft if the new
store is empty.

## Ads

The LevelPlay app key and the supplied banner, interstitial, native, and rewarded ad unit
IDs are set in `app/src/main/res/values/strings.xml`. Banner and interstitial units are
loaded in the writer, matching the Flutter app's ad behavior.

The supplied Meta "Writer Full Screen Ad" and "Writer Banner" placement IDs are also
recorded in `strings.xml`. Configure those placements on the corresponding mediated
LevelPlay ad units in the dashboard. The Android LevelPlay SDK loads with its own ad
unit IDs; passing a Meta placement ID as a LevelPlay ad unit ID will not work.

The banner uses `WRITER_BANNER`; interstitials are requested on the same
copy, paste, clear, share, Help, and About actions as the Flutter app. The mediation
dependencies include Unity Ads and Meta Audience Network.

The Flutter app had no native ad surface or rewarded ad action, so the current writer
layout does not request those two stored ad units.

## Build and test

Use JDK 25 (Android Studio's bundled JBR works) and run:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

The Flutter project's Gradle file refers to a release signing keystore that is absent
from its repository. To publish this as an update to the existing Play Store app, supply
the original signing key and configure release signing separately.
