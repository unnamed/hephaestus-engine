plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(libs.creative.server)

    implementation(project(":hephaestus-runtime-bukkit-api"))
    implementation(project(":hephaestus-runtime-bukkit-adapt-v1_18_R2", "reobf"))
    implementation(project(":hephaestus-reader-blockbench")) {
        exclude(group = "com.google.code.gson", module = "gson")
    }

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}