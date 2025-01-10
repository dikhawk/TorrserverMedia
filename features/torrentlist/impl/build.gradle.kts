import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.dik.torrentlist.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
//        res.srcDirs("src/androidMain/res")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvm()

    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.moduleinjector)
            implementation(projects.features.torrentlist.api)
            implementation(projects.core.torrserverapi.api)
            implementation(projects.features.settings.api)
            implementation(projects.core.common)
            implementation(projects.core.uikit)
            implementation(projects.core.appsettings.api)
            implementation(projects.modules.themoviedb.api)
            implementation(projects.modules.videoFileNameparser)

            implementation(compose.components.resources)
            implementation(libs.compose.adaptive)
            implementation(libs.bundles.decompose)
            implementation(libs.coil.network.ktor)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatformSettings)
            implementation(libs.koin.core)
            implementation(libs.kstore)
            implementation(libs.filekit.core)
            implementation(libs.okio)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.androidx.activityCompose)
        }
    }
}

