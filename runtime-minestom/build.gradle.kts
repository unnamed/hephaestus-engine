repositories {
    maven("https://jitpack.io/")
    mavenCentral()
}

dependencies {
    val minestom = "com.github.Minestom:Minestom:f05b4baa8a"

    api(project(":hephaestus-api"))
    compileOnly(minestom)

    testImplementation(minestom)
    testImplementation(project(":hephaestus-reader-blockbench"))
}