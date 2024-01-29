pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from(files("$rootDir/libs.versions.toml"))
        }
    }
}

rootProject.name = "BleMsg"
include(":presentation")
include(":domain")
include(":model")
