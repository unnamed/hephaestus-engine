rootProject.name = "hephaestus-parent"

includePrefixed("api")
// includePrefixed("plugin")

include("runtime-bukkit:api")
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