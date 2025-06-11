plugins {
    id("multiloader-loader")
    id("fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:${"minecraft_version"()}")

    @Suppress("UnstableApiUsage") mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${"parchment_minecraft"()}:${"parchment_version"()}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_version"()}")

    include(implementation("org.spongepowered:configurate-core:${"configurate_version"()}")!!)
    include(implementation("org.spongepowered:configurate-hocon:${"configurate_version"()}")!!)
    include(implementation("org.spongepowered:configurate-gson:${"configurate_version"()}")!!)
    include(implementation("com.typesafe:config:1.4.3")!!)
    include(implementation("io.leangen.geantyref:geantyref:1.3.16")!!)

    include(modImplementation("eu.pb4:placeholder-api:${"placeholderapi_fabric_version"()}")!!)
    include(modImplementation("eu.pb4:sgui-fabric:${"sgui_version"()}")!!)

    modCompileOnly("dev.emi:trinkets:${"trinkets_version"()}")
    modLocalRuntime("dev.emi:trinkets:${"trinkets_version"()}")

    modCompileOnly("net.luckperms:api:5.4")
    modLocalRuntime("net.luckperms:api:5.4")

    include("net.kyori:option:1.1.0")
}

loom {
    val aw = project(":common").file("src/main/resources/${"mod_id"()}.accesswidener")

    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    @Suppress("UnstableApiUsage") mixin {
        defaultRefmapName.set("${"mod_id"()}.refmap.json")
        useLegacyMixinAp = false
    }

    runs {
        maybeCreate("client").apply {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
            property("mixin.debug.export", "true")
        }

        maybeCreate("server").apply {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
            property("mixin.debug.export", "true")
        }
    }
}

tasks.compileJava {
    source(project(":common").sourceSets["main"].java)
}

tasks.processResources {
    from(project(":common").sourceSets["main"].resources)
}

tasks.remapJar {
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("artifacts"))
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
