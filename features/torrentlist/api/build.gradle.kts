plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.moduleinjector)
                implementation(projects.core.common)
            }
        }
    }
}

