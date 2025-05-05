plugins {
    id("hephaestus.publishing-conventions")
    id("maven-publish")
}

dependencies {
    api(project(":hephaestus-api"))
    api(libs.gson)
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
