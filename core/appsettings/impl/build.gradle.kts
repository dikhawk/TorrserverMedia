import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.dik.appsettnigs.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            implementation(projects.core.appsettings.api)

            implementation(libs.multiplatformSettings)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
