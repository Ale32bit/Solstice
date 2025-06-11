plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
    id("com.gradleup.shadow")
}

val localRuntime: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val shadowDep: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

neoForge {
    version = "neo_version"()

    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")

    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    parchment {
        minecraftVersion = "parchment_minecraft"()
        mappingsVersion = "parchment_version"()
    }

    runs {
        configureEach {
            val capitalized = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

            systemProperty("neoforge.enabledGameTestNamespaces", "mod_id"())
            ideName = "NeoForge $capitalized (${project.path})"
        }

        maybeCreate("client").apply {
            client()
            devLogin = true
        }

        maybeCreate("data").apply {
            data()
        }

        maybeCreate("server").apply {
            server()
            programArgument("nogui")
        }
    }

    mods {
        maybeCreate("mod_id"()).apply {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    jarJar(implementation("org.spongepowered:configurate-core:${"configurate_version"()}")!!)
    jarJar(implementation("org.spongepowered:configurate-hocon:${"configurate_version"()}")!!)
    jarJar(implementation("org.spongepowered:configurate-gson:${"configurate_version"()}")!!)
    jarJar(implementation("com.typesafe:config:1.4.3")!!)
    jarJar(implementation("io.leangen.geantyref:geantyref:1.3.16")!!)
    jarJar(implementation("eu.pb4:placeholderapi:${"placeholderapi_nf_version"()}")!!)
    jarJar(implementation("eu.pb4:sgui-neoforge:${"sgui_version"()}")!!)

    "additionalRuntimeClasspath"("org.spongepowered:configurate-core:${"configurate_version"()}")
    "additionalRuntimeClasspath"("org.spongepowered:configurate-hocon:${"configurate_version"()}")
    "additionalRuntimeClasspath"("org.spongepowered:configurate-gson:${"configurate_version"()}")
    "additionalRuntimeClasspath"("com.typesafe:config:1.4.3")
    "additionalRuntimeClasspath"("io.leangen.geantyref:geantyref:1.3.16")

    compileOnly("net.luckperms:api:5.4")
    localRuntime("net.luckperms:api:5.4")

    jarJar("net.kyori:option:1.1.0")
    shadowDep(project(":common"))
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.shadowJar {
    archiveClassifier = ""
    from(tasks.jarJar)
    configurations.set(listOf(shadowDep))
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("artifacts"))
}

tasks.build.get().finalizedBy(tasks.shadowJar)
sourceSets.main.get().resources { srcDir("src/generated/resources") }
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
