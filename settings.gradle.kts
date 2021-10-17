rootProject.name = "hephaestus-engine"

include("common")
//include("plugin")

//
// Model Subprojects
//
include(":model:creation")

//
// Model Bukkit runtime
//
/*include(":model:runtime-bukkit:core")

arrayOf(
        "v1_14_R1",
        "v1_15_R1",
        "v1_16_R3",
        "v1_17_R1"
).forEach {
    include(":model:runtime-bukkit:adapt-$it")
}*/

//
// Model Minestom runtime
//
include(":model:runtime-minestom")