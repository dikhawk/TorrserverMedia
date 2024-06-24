plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {

    jvm()
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {

            }
        }
    }
}
