enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}

rootProject.name = "TorrServerMedia"

include(
    ":composeApp",
    ":core:moduleinjector",
    ":core:torrserverapi:api",
    ":core:torrserverapi:impl",
    ":core:common",
    ":core:uikit",
    ":core:appsettings:api",
    ":core:appsettings:impl",

    ":features:torrentlist:api",
    ":features:torrentlist:impl",

    ":features:settings:api",
    ":features:settings:impl",

    ":modules:themoviedb:api",
    ":modules:themoviedb:impl",

    ":modules:videoFileNameparser",
)
