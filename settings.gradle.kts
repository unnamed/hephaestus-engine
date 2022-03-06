pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "hephaestus-parent"

includePrefixed("api")
includePrefixed("reader-blockbench")

include("runtime-bukkit:api")
include("runtime-bukkit:test-plugin")
arrayOf(
    "v1_18_R2"
).forEach {
    include(":runtime-bukkit:adapt-$it")
}

includePrefixed("runtime-minestom")

fun includePrefixed(name: String) {
    include("hephaestus-$name")
    project(":hephaestus-$name").projectDir = file(name)
}