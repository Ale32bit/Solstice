plugins {
    id("multiloader-common")
}

val commonJava by configurations.creating {
    isCanBeResolved = true
}

val commonResources by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("${"mod_group_id"()}:${"mod_id"()}")
        }
    }

    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.processResources {
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}

tasks.named<Javadoc>("javadoc").configure {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(configurations["commonJava"])
    from(configurations["commonJava"])
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
