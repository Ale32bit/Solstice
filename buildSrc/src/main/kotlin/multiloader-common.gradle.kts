plugins {
    id("java-library")
    id("maven-publish")
}

base {
    archivesName = "${"mod_id"()}-${project.name}-${"minecraft_version"()}"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of("java_version"())
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()

    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.blamejared.com")
    maven("https://mvn.devos.one/snapshots")
    maven("https://maven.ladysnake.org/releases")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.nucleoid.xyz")
    maven("https://maven.alexdevs.me/releases")
    maven("https://maven.stardustmodding.org/releases")
}

listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations[variant].outgoing {
        capability("${"mod_group_id"()}:${base.archivesName.get()}:${"mod_version"()}")
        capability("${"mod_group_id"()}:${"mod_id"()}-${project.name}-${"minecraft_version"()}:${"mod_version"()}")
        capability("${"mod_group_id"()}:${"mod_id"()}:${"mod_version"()}")
    }

//    publishing.publications.configureEach {
//        suppressPomMetadataWarningsFor(variant)
//    }
}

tasks.named<Jar>("sourcesJar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${"mod_id"()}" }
    }
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${"mod_id"()}" }
    }

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "mod_name"(),
                "Specification-Vendor" to "mod_authors"(),
                "Specification-Version" to "mod_version"(),
                "Implementation-Title" to project.name,
                "Implementation-Version" to "mod_version"(),
                "Implementation-Vendor" to "mod_authors"(),
                "Built-On-Minecraft" to "minecraft_version"()
            )
        )
    }
}

tasks.processResources {
    var expandProps = mapOf(
        "mod_version" to "mod_version"(),
        "mod_group_id" to "mod_group_id"(),
        "minecraft_version" to "minecraft_version"(),
        "minecraft_version_range" to "minecraft_version_range"(),
        "fabric_version" to "fabric_version"(),
        "fabric_loader_version" to "fabric_loader_version"(),
        "mod_name" to "mod_name"(),
        "mod_authors" to "mod_authors"(),
        "mod_id" to "mod_id"(),
        "mod_license" to "mod_license"(),
        "mod_description" to "mod_description"(),
        "neo_version" to "neo_version"(),
        "neo_version_range" to "neo_version_range"(),
        "neo_loader_version_range" to "neo_loader_version_range"(),
        "mod_credits" to "mod_credits"(),
        "java_version" to "java_version"()
    )

    filesMatching(
        listOf(
            "pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json"
        )
    ) {
        expand(expandProps)
    }

    inputs.properties(expandProps)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        maven {
            mavenLocal()
        }
    }
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
