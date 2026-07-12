pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "KikuGie Releases"
            url = uri("https://maven.kikugie.dev/releases")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
    id("dev.kikugie.loom-back-compat") version "0.4"
}

stonecutter {
    create(rootProject) {
        versions("1.21.1", "1.21.4", "1.21.11", "26.2")
        vcsVersion = "1.21.1"
    }
}

