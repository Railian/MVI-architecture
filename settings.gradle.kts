enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google{
            mavenContent {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "MVI-architecture"
include(":library:contract")
include(":library:core")
include(":library:core:implementation")
include(":library:shared-config")
include(":library:implementation:mock")
include(":library:implementation:simple")
include(":library:implementation:embedded")
include(":library:implementation:constructed")
include(":library:implementation:dual-state-simple")
include(":library:implementation:dual-state-embedded")
include(":library:implementation:dual-state-constructed")
include(":library:extension:android-compose")
include(":library:extension:koin-android-compose")
