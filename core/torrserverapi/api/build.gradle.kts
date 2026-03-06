import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'
    android {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
                }
            }
        }

        namespace = "com.dik.torrserverapi.api"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.moduleinjector)
                implementation(projects.core.common)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        androidMain.dependencies {

        }

        jvmMain.dependencies {

        }
    }
}