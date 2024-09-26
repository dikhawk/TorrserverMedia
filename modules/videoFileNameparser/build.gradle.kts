//That libs ChatGpt converted to Kotlin. Original https://github.com/scttcper/video-filename-parser/

plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
    }
}
