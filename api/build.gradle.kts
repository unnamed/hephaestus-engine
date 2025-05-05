plugins {
    id("hephaestus.publishing-conventions")
    id("maven-publish")
}

dependencies {
    api(libs.annotations)
    api(libs.creative.api)
    api(libs.adventure.api)
    api(libs.gson)
    api(libs.mocha)
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
