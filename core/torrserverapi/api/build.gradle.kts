
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.moduleinjector)
                implementation(projects.core.common)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}