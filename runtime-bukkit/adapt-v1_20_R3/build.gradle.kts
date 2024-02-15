plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("hephaestus.publishing-conventions")
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

repositories {
    maven("https://libraries.minecraft.net/")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    implementation(project(":hephaestus-runtime-bukkit-api"))
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            artifact(tasks.reobfJar) {
                classifier = "reobf"
            }
        }
    }
}