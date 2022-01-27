rootProject.name = "hephaestus-parent"

includePrefixed("api")

include("runtime-bukkit:api")
include("runtime-bukkit:test-plugin")
arrayOf(
    "v1_17_R1"
).forEach {
    include(":runtime-bukkit:adapt-$it")
}

includePrefixed("runtime-minestom")

fun includePrefixed(name: String) {
    include("hephaestus-$name")
    project(":hephaestus-$name").projectDir = file(name)
}