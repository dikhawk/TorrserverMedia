plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.moduleinjector)
            implementation(projects.core.common)
        }
    }
}
