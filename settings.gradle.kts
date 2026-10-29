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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // 本地 Maven 仓库 - 用于 AAR
        maven { url = uri("${rootDir}/repo") }
    }
}

rootProject.name = "AppBase"
include(":app")
include(":lib_widget")
include(":lib_util")
include(":lib_base")
include(":lib_aar")
