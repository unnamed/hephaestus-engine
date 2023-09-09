plugins {
    id("hephaestus.publishing-conventions")
}

repositories {
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly(libs.minestomce)

    testImplementation(libs.creative.api)
    testImplementation(libs.creative.server)
    testImplementation(libs.creative.serializer.minecraft)

    testImplementation(libs.minestomce)
    testImplementation(project(":hephaestus-reader-blockbench"))
}