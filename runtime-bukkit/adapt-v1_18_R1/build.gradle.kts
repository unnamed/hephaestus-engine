repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation(project(":runtime-bukkit:api"))
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.18.1-R0.1-SNAPSHOT:remapped-mojang")
}