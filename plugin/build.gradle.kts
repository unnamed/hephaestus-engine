plugins {
    id("com.github.johnrengelman.shadow") version("7.0.0")
}

tasks.withType<JavaCompile> {
    val version = if (JavaVersion.current() >= JavaVersion.VERSION_16) { "16" } else { "8" }
    sourceCompatibility = version
    targetCompatibility = version
}

repositories {
    mavenLocal()
    maven("https://repo.unnamed.team/repository/unnamed-public/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    api(project(":runtime-bukkit"))
    implementation("team.unnamed:molang:0.1.0")

    implementation("org.jetbrains:annotations:22.0.0")
    implementation("me.fixeddev:commandflow-bukkit:0.4.0")
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.lumine.xikage:MythicMobs:4.9.1")

    // adaption modules
    arrayOf(
            "v1_14_R1",
            "v1_15_R1",
            "v1_16_R3"
    ).forEach {
        runtimeOnly(project(":runtime-bukkit:adapt-$it"))
    }

    if (JavaVersion.current() >= JavaVersion.VERSION_16) {
        runtimeOnly(project(":runtime-bukkit:adapt-v1_17_R1"))
    }
}