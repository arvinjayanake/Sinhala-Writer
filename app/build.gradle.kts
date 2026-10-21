plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.arvin.sinhalawriter"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.arvin.sinhalawriter"
        minSdk = 24
        targetSdk = 37
        versionCode = 19
        versionName = "2.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.browser)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.ads.identifier)
    implementation(libs.mediation.sdk)
    implementation(libs.facebook.adapter)
    implementation(libs.audience.network.sdk)
    implementation(libs.unityads.adapter)
    implementation(libs.unity.ads)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// Keep release artifacts tied to the app version and monotonically increasing build number.
val releaseArtifactName = "sw_${android.defaultConfig.versionName}_${android.defaultConfig.versionCode}"
base {
    archivesName = releaseArtifactName
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set("$releaseArtifactName.apk")
        }
    }
}

// AGP's bundle filename has no VariantOutput override. Export an exact, stable filename.
val exportReleaseBundle = tasks.register<Copy>("exportReleaseBundle") {
    from(layout.buildDirectory.file("outputs/bundle/release/$releaseArtifactName-release.aab"))
    into(layout.buildDirectory.dir("outputs/release"))
    rename(".*\\.aab", "$releaseArtifactName.aab")
}
tasks.matching { it.name == "bundleRelease" }.configureEach {
    finalizedBy(exportReleaseBundle)
}