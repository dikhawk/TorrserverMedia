import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.stabilityAnalyzer)
}

kotlin {
    jvm()

    android {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
                }
            }
        }

        namespace = "com.dik.torrentlist.impl"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources.enable = true
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

            implementation(libs.compose.components.resources)
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
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.activityCompose)
        }

        commonTest.dependencies {
            implementation(libs.bundles.testing)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.compose.adaptive)
        }
    }
}
