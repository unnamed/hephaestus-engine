repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://jitpack.io/")
    mavenCentral()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

dependencies {
    val minestom = "com.github.Minestom:Minestom:48d8cbf10e"

    api(project(":core"))
    compileOnly(minestom)

    testImplementation(minestom)
}