repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://jitpack.io/")
    mavenCentral()
}

tasks.withType<JavaCompile>() {
    options.release.set(16)
}

dependencies {
    val minestom = "com.github.Minestom:Minestom:d53ef36586"

    api(project(":common"))
    compileOnly(minestom)

    testImplementation(minestom)
}