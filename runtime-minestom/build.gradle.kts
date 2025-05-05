plugins {
    id("hephaestus.publishing-conventions")
    id("maven-publish")
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.withType<Sign> {
    onlyIf {
        gradle.taskGraph.hasTask("publish") && !gradle.taskGraph.hasTask("publishToMavenLocal")
    }
}
