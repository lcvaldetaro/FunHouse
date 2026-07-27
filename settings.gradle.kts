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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FunHouseMultiplatform"
include(":composeApp")
include(":shared:common")
include(":feature:blackjack")
include(":feature:castle-kotlin")
include(":feature:chess")
include(":feature:chimaera-kotlin")
include(":feature:classic-arcades")
include(":feature:colossal-cave-adventure-kotlin")
include(":feature:craps")
include(":feature:dinkum-kotlin")
include(":feature:eliza-kotlin")
include(":feature:funhouse-engine-kotlin")
include(":feature:hangman-kotlin")
include(":feature:mistery-mansion-kotlin")
include(":feature:poker")
include(":feature:roulette")
include(":feature:secret-forest-kotlin")
include(":feature:slot-machine")
include(":feature:space-wars-kotlin")
include(":feature:tetric")
include(":feature:wander-engine-kotlin")
include(":feature:wizards-castle-kotlin")
