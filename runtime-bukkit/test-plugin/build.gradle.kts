plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("com.gradleup.shadow") version "8.3.6"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // server api
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    // resource-pack plugin api
    compileOnly("team.unnamed:creative-central-api:1.4.0")

    // hephaestus-engine dependencies
    implementation(project(":hephaestus-api"))
    implementation(project(":hephaestus-reader-blockbench"))
    implementation(project(":hephaestus-runtime-bukkit-api"))
    implementation(project(":hephaestus-runtime-bukkit-adapt-v1_21_4", configuration = "reobf"))
}

tasks {
    runServer {
        downloadPlugins {
            //modrinth("central", "1.3.0") // creative-central
        }

        minecraftVersion("1.21.4")
    }
    shadowJar {
        dependencies {
            exclude(dependency("team.unnamed:creative-api"))
            exclude(dependency("com.google.code.gson:gson"))
            exclude(dependency("net.kyori:adventure-api"))
            exclude(dependency("net.kyori:examination-.*"))
            exclude(dependency("org.jetbrains:annotations"))
        }
    }
}