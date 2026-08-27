import com.modrinth.minotaur.dependencies.ModDependency
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("dev.kikugie.loom-back-compat")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
    id("net.darkhax.curseforgegradle") version "1.2.30"
}

// DO NOT set group = ...! Loom/Stonecutter manage it per-subproject; set groupId on the publication instead.
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

// Snapshot/alpha versions are marked with a `-alpha` or `-SNAPSHOT` pre-release tag in `mod.version`
// (e.g. "1.10.0-alpha.1"). Presence of that tag drives Modrinth's versionType and the Maven repo used.
val isAlpha: Boolean = (property("mod.version") as String).let {
    it.contains("-alpha") || it.contains("-SNAPSHOT")
}

val mvnVersion = if (isAlpha) {
    version.toString()
        .replaceFirst("-alpha", "") + "-SNAPSHOT"
}
else {
    version as String
}


val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else                        -> JavaVersion.VERSION_21
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList()
    .orEmpty()
    .map { it.toString() }
    .toSet()

repositories {
    mavenLocal()
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://maven.nucleoid.xyz") }
    maven { url = uri("https://maven.terraformersmc.com/") }
    maven { url = uri("https://maven.ladysnake.org/releases") }
    maven { url = uri("https://api.modrinth.com/maven") }
}

dependencies {

    minecraft("com.mojang:minecraft:${sc.current.version}")
    if (sc.current.parsed >= "26.1") {
        loomx.applyMojangMappings()
    }
    else {
        mappings(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${sc.current.version}:${property("parchment")}@zip")
        })
    }

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation(include("org.spongepowered:configurate-core:${property("deps.configurate")}")!!)
    modImplementation(include("org.spongepowered:configurate-hocon:${property("deps.configurate")}")!!)
    modImplementation(include("org.spongepowered:configurate-gson:${property("deps.configurate")}")!!)
    include("com.typesafe:config:1.4.3")
    include("io.leangen.geantyref:geantyref:1.3.16")

    modImplementation(include("me.lucko:fabric-permissions-api:${property("deps.permissions_api")}")!!)
    modImplementation(include("eu.pb4:placeholder-api:${property("deps.placeholderapi")}")!!)
    modImplementation(include("eu.pb4:sgui:${property("deps.sgui")}")!!)
    modImplementation(include("eu.pb4:common-economy-api:${property("deps.commoneconomy")}")!!)

    if (sc.current.parsed >= "26.2") {
        modCompileOnly("maven.modrinth:trinkets-updated:${property("deps.trinkets")}")
    }
    else {
        modCompileOnly("dev.emi:trinkets:${property("deps.trinkets")}")
    }
    modCompileOnly("net.luckperms:api:${project.property("deps.luckperms")}")
    modRuntimeOnly("net.luckperms:api:${project.property("deps.luckperms")}")
    modCompileOnly("maven.modrinth:vanish:${project.property("deps.vanish")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection

    accessWidenerPath = sc.process(
        rootProject.file("src/main/resources/solstice.ct"),
        ".gradle/processed.ct"
    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run") // Shares the run directory between versions
        jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}


tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }
}


tasks.jar {
    val archivesName = base.archivesName.get()
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${archivesName}" }
    }
}

// if -PprebuiltJar is set then directly point to the jars so we don't run tasks, this is useful for parallel deploys
val prebuiltJar = providers.gradleProperty("prebuiltJar").isPresent
val modJarFile: Any = if (prebuiltJar) {
    layout.buildDirectory.file("libs/${base.archivesName.get()}-${version}.jar").get().asFile
}
else {
    loomx.modJar.flatMap { it.archiveFile }
}

modrinth {
    token.set(providers.environmentVariable("MODRINTH_TOKEN"))
    projectId = "uIvrDZas"
    uploadFile.set(modJarFile)
    changelog.set(providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText)
    versionType.set(if (isAlpha) "alpha" else "release")
    gameVersions = compatibleVersions
    loaders = listOf("fabric")
    dependencies = listOf(ModDependency("P7dR8mSH", "required"))
    // P7dR8mSH - fabric-api
}

tasks.register<TaskPublishCurseForge>("curseforge") {
    description = "Curseforge upload"
    group = "publishing"
    apiToken = providers.environmentVariable("CURSEFORGE_TOKEN").orNull

    disableVersionDetection()

    val mainFile = upload("1149875", modJarFile)
    mainFile.changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText.orElse("")
    mainFile.changelogType = "markdown"
    mainFile.releaseType = if (isAlpha) "alpha" else "release"
    mainFile.addModLoader("Fabric")
    mainFile.addEnvironment("Server")
    mainFile.addRequirement("fabric-api")
    compatibleVersions.forEach {
        mainFile.addGameVersion(it)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = property("mod.group") as String
            artifactId = property("mod.id") as String
            version = mvnVersion
            if (prebuiltJar) {
                artifact(modJarFile)
            }
            else {
                from(components["java"])
            }
        }
    }
    repositories {
        maven {
            name = "AlexDevsRepo"
            url = uri(if (isAlpha) "https://maven.alexdevs.me/snapshots" else "https://maven.alexdevs.me/releases")
            credentials {
                username = providers.environmentVariable("MAVEN_USERNAME").orNull
                password = providers.environmentVariable("MAVEN_PASSWORD").orNull
            }
        }
    }
}

tasks.register("maven") {
    group = "publishing"
    description = "Alias for publish"
    dependsOn(tasks.named("publish"))
}