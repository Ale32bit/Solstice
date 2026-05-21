pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Stonecutter"
            url = uri("https://maven.kikugie.dev/snapshots")
            content { includeGroup("dev.kikugie") }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.21.1", "1.21.4", "1.21.11")
        vcsVersion = "1.21.1"
    }
}

