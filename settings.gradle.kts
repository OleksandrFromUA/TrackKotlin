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



include(":features")
include(":features:map")
include(":features:tracker")
include(":features:auth")
include(":appMap")
include(":data")
include(":dataBase")
