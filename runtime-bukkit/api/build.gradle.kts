plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("hephaestus.publishing-conventions")
    id("maven-publish")
}

dependencies {
    api(project(":hephaestus-api"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
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
