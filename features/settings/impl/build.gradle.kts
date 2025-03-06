import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.dik.settings.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
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
            implementation(projects.core.common)
            implementation(projects.features.settings.api)
            implementation(projects.core.uikit)
            implementation(projects.core.torrserverapi.api)
            implementation(projects.core.appsettings.api)

            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.bundles.decompose)
            implementation(libs.koin.core)
            implementation(compose.preview)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(compose.preview)
        }

        jvmMain.dependencies {
            implementation(compose.uiTooling)
            implementation(compose.preview)
        }

        commonTest.dependencies {
            implementation(libs.bundles.testing)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.compose.adaptive)
        }
    }
}
