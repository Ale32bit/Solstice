plugins {
    id("multiloader-common")
}

dependencies {
    compileOnly(project(":common"))
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
