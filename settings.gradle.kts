rootProject.name = "hephaestus-engine"

include("core")
include("plugin")

//
// Model Bukkit runtime
//
include(":runtime-bukkit:core")

arrayOf(
        "v1_14_R1",
        "v1_15_R1",
        "v1_16_R3",
        "v1_17_R1"
).forEach {
    include(":runtime-bukkit:adapt-$it")
}

//
// Model Minestom runtime
//
include(":runtime-minestom")