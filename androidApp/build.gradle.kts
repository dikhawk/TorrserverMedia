import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dik.torrservermedia"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        applicationId = "com.dik.torrservermedia.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            val properties = gradleLocalProperties(rootDir, providers)

            storeFile = file(properties.getProperty("KEYSTORE_FILE") as String)

            if (storeFile?.exists() != true) throw NullPointerException("File store is not found, check config")

            storePassword = properties.getProperty("KEYSTORE_PASSWORD") as String
            keyAlias = properties.getProperty("KEY_ALIAS") as String
            keyPassword = properties.getProperty("KEY_PASSWORD") as String
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isShrinkResources = true
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.activityCompose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.decompose.core)
    implementation(libs.koin.android)
    implementation(libs.accompanist.permissions)
    implementation(libs.android.lifecycle.runtime)

    implementation(projects.core.common)
    implementation(projects.shared)
}