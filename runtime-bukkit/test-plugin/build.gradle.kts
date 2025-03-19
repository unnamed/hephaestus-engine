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

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mocha, hephaestus-engine
    maven("https://papermc.io/repo/repository/maven-public/") // paper-api
    maven("https://maven.citizensnpcs.co/repo") // Citizens
    maven("https://repo.unnamed.team/repository/unnamed-public/") // command-flow
    maven("https://mvn.lumine.io/repository/maven-public/") // MythicMobs
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

    // plugin dependencies
    implementation("me.fixeddev:commandflow-universal:0.6.0") // command-flow
    implementation("me.fixeddev:commandflow-bukkit:0.6.0") // command-flow

    // hooks
    compileOnly("io.lumine:Mythic-Dist:5.3.5") // MythicMobs
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") { // Citizens
        exclude(group = "*", module = "*")
    }
}

tasks {
    runServer {
        downloadPlugins {
            //modrinth("central", "1.3.0") // creative-central
            url("https://ci.citizensnpcs.co/job/Citizens2/lastSuccessfulBuild/artifact/dist/target/Citizens-2.0.37-b3756.jar") // Citizens
        }

        minecraftVersion("1.21.4")
    }
    shadowJar {
        val pkg = "team.unnamed.hephaestus.bukkit.plugin.lib"
        relocate("me.fixeddev.commandflow", "$pkg.commandflow")

        dependencies {
            exclude(dependency("team.unnamed:creative-api"))
            exclude(dependency("com.google.code.gson:gson"))
            exclude(dependency("net.kyori:adventure-api"))
            exclude(dependency("net.kyori:examination-.*"))
            exclude(dependency("org.jetbrains:annotations"))
        }
    }
}