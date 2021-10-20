repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://jitpack.io/")
    mavenCentral()
}

dependencies {
    val minestom = "com.github.Minestom:Minestom:d53ef36586"

    api(project(":core"))
    compileOnly(minestom)

    testImplementation(minestom)
}