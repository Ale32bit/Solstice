plugins {
    id("fabric-loom") version "1.8-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.88" apply false
    id("com.gradleup.shadow") version "9.0.0-beta15" apply false
}

tasks.register("build") {
    group = "all"

    dependsOn(project(":fabric").tasks.named("build"))
    dependsOn(project(":neoforge").tasks.named("build"))
}
