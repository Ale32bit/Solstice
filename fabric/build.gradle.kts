plugins {
    id("multiloader-loader")
    id("fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:${"minecraft_version"()}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
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
}

loom {
    val aw = project(":common").file("src/main/resources/${"mod_id"()}.accesswidener")

    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    mixin {
        defaultRefmapName.set("${"mod_id"()}.refmap.json")
    }

    runs {
        maybeCreate("client").apply {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("runs/client")
        }

        maybeCreate("server").apply {
            server()
            setConfigName("Fabric Server")
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
