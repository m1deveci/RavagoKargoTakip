pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:8.7.2") // Android eklentisi sürümü
            }
            if (requested.id.id == "org.jetbrains.kotlin.android") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0") // Kotlin eklentisi sürümü
            }
        }
    }
}

rootProject.name = "RavagoKargoTakip"
include(":app")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}