repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central/")
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
}