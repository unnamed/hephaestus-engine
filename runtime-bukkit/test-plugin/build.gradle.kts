plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    // server api
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    // resource-pack plugin api
    compileOnly("team.unnamed:creative-central-api:0.10.0-SNAPSHOT")

    // hephaestus-engine dependencies
    implementation(project(":hephaestus-api"))
    implementation(project(":hephaestus-reader-blockbench"))
    implementation(project(":hephaestus-runtime-bukkit-api"))
    implementation(project(":hephaestus-runtime-bukkit-adapt-v1_20_R2", configuration = "reobf"))
}

tasks {
    shadowJar {
        dependencies {
            exclude(dependency("team.unnamed:creative-api"))
            exclude(dependency("com.google.code.gson:gson"))
            exclude(dependency("net.kyori:adventure-.*"))
            exclude(dependency("net.kyori:examination-.*"))
            exclude(dependency("org.jetbrains:annotations"))
        }
    }
}