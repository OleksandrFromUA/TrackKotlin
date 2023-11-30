pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tracker"
include(":app")
include(":appMap")
include(":core:data")
include(":core:dataBase")
include(":features:auth")
include(":features:map")
include(":features:tracker")
