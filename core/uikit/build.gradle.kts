plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'

    linuxX64() {
        /* Specify additional settings for the 'linux' target here */
    }

    sourceSets {
        commonMain {
            dependencies {
//                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}
