repositories {
    maven("https://jitpack.io/")
    mavenCentral()
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly(libs.minestom)

    testImplementation(libs.minestom)
    testImplementation(project(":hephaestus-reader-blockbench"))
}