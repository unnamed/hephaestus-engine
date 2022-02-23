plugins {
    id("io.papermc.paperweight.userdev") version "1.3.4"
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
    implementation(project(":runtime-bukkit:api"))
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}