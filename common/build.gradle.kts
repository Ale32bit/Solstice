plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
}

neoForge {
    neoFormVersion = "neoform_version"()

    val at = file("src/main/resources/META-INF/accesstransformer.cfg")

    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    parchment {
        minecraftVersion = "parchment_minecraft"()
        mappingsVersion = "parchment_version"()
    }
}

dependencies {
    compileOnly("org.ow2.asm:asm-tree:9.8")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")

    compileOnly("org.spongepowered:configurate-core:${"configurate_version"()}")
    compileOnly("org.spongepowered:configurate-hocon:${"configurate_version"()}")
    compileOnly("org.spongepowered:configurate-gson:${"configurate_version"()}")
    compileOnly("com.typesafe:config:1.4.3")
    compileOnly("io.leangen.geantyref:geantyref:1.3.16")
    compileOnly("eu.pb4:placeholderapi:${"placeholderapi_nf_version"()}")
    compileOnly("eu.pb4:sgui-neoforge:${"sgui_version"()}")
    compileOnly("net.luckperms:api:5.4")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    add(commonJava.name, sourceSets.main.get().java.sourceDirectories.singleFile)
    add(commonResources.name, sourceSets.main.get().resources.sourceDirectories.singleFile)
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
