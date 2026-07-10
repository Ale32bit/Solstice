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
    shared {
        versions("1.21.1", "1.21.4", "1.21.11", "26.2")
        mapBuilds { _, node ->
            if (node.parsed >= "26.1") "build-unobfuscated.gradle.kts" else "build.gradle.kts"
        }
        vcsVersion.set("1.21.1")
    }
    create(rootProject)
}

