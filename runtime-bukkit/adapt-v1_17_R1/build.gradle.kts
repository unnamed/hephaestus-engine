tasks.withType<JavaCompile> {
    sourceCompatibility = "16"
    targetCompatibility = "16"
}

dependencies {
    implementation(project(":runtime-bukkit"))
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
}