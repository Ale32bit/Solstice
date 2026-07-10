import com.modrinth.minotaur.dependencies.ModDependency
plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
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
    maven { url = uri("https://maven.nucleoid.xyz") }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://maven.ladysnake.org/releases")
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
}
// 26.1+ ships unobfuscated, so named mappings equal official ones; only the header differs.
val accessWidener = layout.buildDirectory.file("solstice-official.accesswidener").get().asFile.apply {
    parentFile.mkdirs()
    writeText(
        rootProject.file("src/main/resources/solstice.accesswidener").readText()
            .replaceFirst("accessWidener v2 named", "accessWidener v2 official")
    )
}
loom {
    accessWidenerPath = accessWidener
}
dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    include(implementation("org.spongepowered:configurate-core:${property("configurate_version")}")!!)
    include(implementation("org.spongepowered:configurate-hocon:${property("configurate_version")}")!!)
    include(implementation("org.spongepowered:configurate-gson:${property("configurate_version")}")!!)
    include("com.typesafe:config:1.4.3")
    include("io.leangen.geantyref:geantyref:1.3.16")
    include(implementation("me.lucko:fabric-permissions-api:${property("permissions_api_version")}")!!)
    include(implementation("eu.pb4:placeholder-api:${property("placeholderapi_version")}")!!)
    include(implementation("eu.pb4:sgui:${property("sgui_version")}")!!)
    implementation(include("eu.pb4:common-economy-api:${property("commoneconomy_version")}")!!)
    compileOnly("maven.modrinth:trinkets-updated:${property("trinkets_version")}")
    compileOnly("net.luckperms:api:5.4")
    runtimeOnly("net.luckperms:api:5.4")
    compileOnly("maven.modrinth:vanish:${project.property("vanish_version")}")
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
    uploadFile = tasks["jar"]
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
    replacements.string(current.parsed >= "26.2") {
        replace("Placeholders.register(", "Placeholders.registerCommon(")
        replace("dev.emi.trinkets", "eu.pb4.trinkets")
    }
    replacements.string(current.parsed >= "1.21.11") {
        replace("player.getServer()", "player.level().getServer()")
        replace("sourcePlayer.getServer()", "sourcePlayer.level().getServer()")
        replace("dimension().location()", "dimension().identifier()")
    }
}
