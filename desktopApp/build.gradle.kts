import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.hotreload)
    alias(libs.plugins.kotlin.jvm)
}

compose.desktop {
    application {
        mainClass = "com.dik.torrservermedia.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "com.dik.torrservermedia.desktopApp"
            packageVersion = "1.0.0"

            linux {
                modules("jdk.security.auth")
//                iconFile.set(project.file("appIcons/LinuxIcon.png"))
            }
            windows {
//                iconFile.set(project.file("appIcons/WindowsIcon.ico"))
            }
            macOS {
//                iconFile.set(project.file("appIcons/MacosIcon.icns"))
//                bundleID = "org.company.app.desktopApp"
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.dik.torrservermedia"
    generateResClass = auto
}

dependencies {
    implementation(projects.shared)

    implementation(libs.bundles.compose)
    implementation(libs.decompose.core)
    implementation(libs.decompose.compose.ext)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.components.resources)
}
