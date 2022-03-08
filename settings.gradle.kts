pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "hephaestus-parent"

includePrefixed("api")
includePrefixed("reader-blockbench")
includePrefixed("runtime-bukkit:api")
includePrefixed("runtime-bukkit:adapt-v1_18_R2")
includePrefixed("runtime-bukkit:test-plugin")
// includePrefixed("runtime-minestom")

fun includePrefixed(name: String) {
    val kebabName = name.replace(':', '-')
    val path = name.replace(':', '/')

    include("hephaestus-$kebabName")
    project(":hephaestus-$kebabName").projectDir = file(path)
}