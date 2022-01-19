rootProject.name = "hephaestus-parent"

includePrefixed("api")
// includePrefixed("plugin")

// includePrefixed("runtime-bukkit")
// arrayOf(
//         "v1_14_R1",
//         "v1_15_R1",
//         "v1_16_R3",
//         "v1_17_R1"
// ).forEach {
//     include(":runtime-bukkit:adapt-$it")
// }

includePrefixed("runtime-minestom")

fun includePrefixed(name: String) {
    include("hephaestus-$name")
    project(":hephaestus-$name").projectDir = file(name)
}