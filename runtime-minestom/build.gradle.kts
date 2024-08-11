plugins {
    id("hephaestus.publishing-conventions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly(libs.minestom)

    testImplementation(libs.creative.api)
    testImplementation(libs.creative.server)
    testImplementation(libs.creative.serializer.minecraft)

    testImplementation(libs.minestom)
    testImplementation("org.slf4j:slf4j-jdk14:2.0.12")
    testImplementation(project(":hephaestus-reader-blockbench"))
}