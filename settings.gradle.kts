pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		maven("https://maven.neoforged.net/releases")
		maven("https://repo.spongepowered.org/repository/maven-public")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("common")
include("fabric")
include("neoforge")
