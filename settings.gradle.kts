rootProject.name = "fursuit-tv-sdk"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
    
    versionCatalogs {
        create("libs")
    }
}
