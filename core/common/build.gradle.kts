plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'

    linuxArm64()
    linuxX64() {
        /* Specify additional settings for the 'linux' target here */
    }

    sourceSets {
        commonMain {
            dependencies {
//                implementation(libs.kotlinx.coroutines)
                implementation(compose.runtime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.decompose.core)
            }
        }
    }
}