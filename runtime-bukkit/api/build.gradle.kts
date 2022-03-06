plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("hephaestus.publishing-conventions")
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
}