plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "hephaestus-parent"

includePrefixed("api")
includePrefixed("reader-blockbench")
includePrefixed("runtime-bukkit:api")
includePrefixed("runtime-bukkit:adapt-v1_21_4")
includePrefixed("runtime-bukkit:test-plugin")
includePrefixed("runtime-minestom")

fun includePrefixed(name: String) {
    val kebabName = name.replace(':', '-')
    val path = name.replace(':', '/')

    include("hephaestus-$kebabName")
    project(":hephaestus-$kebabName").projectDir = file(path)
}