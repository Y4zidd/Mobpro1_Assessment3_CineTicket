import java.util.Properties
import java.io.FileInputStream
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

val localProperties: Properties = Properties()
val localPropertiesFile: File = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
//noinspection SpellCheckingInspection
val tmdbApiKey: String = localProperties.getProperty("TMDB_API_KEY") ?: ""

android {
    namespace = "com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Navigation & Images
    implementation(libs.navigation.compose)
    implementation(libs.coil.compose)

    // Firebase & Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    
    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play)
    implementation(libs.googleid)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
