import com.modrinth.minotaur.dependencies.ModDependency
plugins {
    id("fabric-loom") version "1.14-SNAPSHOT"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
    id("dev.kikugie.stonecutter")
}
version = "${property("mod_version")}+${property("minecraft_version")}"
group = property("maven_group") as String
base {
    archivesName.set(property("archives_base_name") as String)
}
repositories {
    mavenLocal()
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
    }
    maven { url = uri("https://maven.nucleoid.xyz") }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://maven.ladysnake.org/releases")
    }
}
val accessWidener = rootProject.file("src/main/resources/solstice.accesswidener")
loom {
    accessWidenerPath = accessWidener
}
dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("minecraft_version")}:${property("parchment_mappings")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    include(implementation("org.spongepowered:configurate-core:${property("configurate_version")}")!!)
    include(implementation("org.spongepowered:configurate-hocon:${property("configurate_version")}")!!)
    include(implementation("org.spongepowered:configurate-gson:${property("configurate_version")}")!!)
    include("com.typesafe:config:1.4.3")
    include("io.leangen.geantyref:geantyref:1.3.16")
    include(modImplementation("me.lucko:fabric-permissions-api:${property("permissions_api_version")}")!!)
    include(modImplementation("eu.pb4:placeholder-api:${property("placeholderapi_version")}")!!)
    include(modImplementation("eu.pb4:sgui:${property("sgui_version")}")!!)
    modImplementation(include("eu.pb4:common-economy-api:${property("commoneconomy_version")}")!!)
    modCompileOnly("dev.emi:trinkets:${property("trinkets_version")}")
    modCompileOnly("net.luckperms:api:5.4")
    modRuntimeOnly("net.luckperms:api:5.4")
}
tasks.processResources {
    val mcConstraint = project.property("minecraft_constraint") as String
    val javaVer = project.property("java_version") as String
    inputs.property("version", project.version)
    inputs.property("minecraft_constraint", mcConstraint)
    inputs.property("java_version", javaVer)
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to project.version,
            "minecraft_constraint" to mcConstraint,
            "java_version" to javaVer
        ))
    }
}
val javaVersion = (property("java_version") as String).toInt()
tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersion
}
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}
tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}
modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "uIvrDZas"
    uploadFile = tasks["remapJar"]
    gameVersions = listOf(property("minecraft_version") as String)
    loaders = listOf("fabric")
    dependencies = listOf(ModDependency("P7dR8mSH", "required"))
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = property("archives_base_name") as String
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "AlexDevsRepo"
            url = uri("https://maven.alexdevs.me/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

stonecutter {
    replacements.string(current.parsed >= "1.21.11") {
//        replace("player.level()", "player.serverLevel()")
//        replace("sourcePlayer.serverLevel()", "sourcePlayer.level()")
        replace("player.getServer()", "player.level().getServer()")
        replace("sourcePlayer.getServer()", "sourcePlayer.level().getServer()")
        replace("dimension().location()", "dimension().identifier()")
    }
}