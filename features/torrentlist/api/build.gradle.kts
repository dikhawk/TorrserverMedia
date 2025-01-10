plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm() // Create a JVM target with the default name 'jvm'

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.moduleinjector)
                implementation(projects.core.common)
                implementation(compose.runtime)
                implementation(libs.bundles.decompose)
            }
        }
    }
}

