dependencies {
    implementation(project(":runtime-bukkit:api"))
    implementation(project(":runtime-bukkit:adapt-v1_17_R1"))
    implementation(project(":hephaestus-reader-blockbench"))

    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
}