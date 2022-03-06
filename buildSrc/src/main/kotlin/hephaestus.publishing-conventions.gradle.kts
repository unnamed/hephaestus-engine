plugins {
    id("hephaestus.common-conventions")
    `maven-publish`
}

val snapshotRepository: String by project
val releaseRepository: String by project

publishing {
    repositories {
        maven {
            val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")

            url = uri(if (isSnapshot) { snapshotRepository } else { releaseRepository })
            credentials {
                username = project.properties["UNNAMED_REPO_USER"] as String?
                    ?: System.getenv("REPO_USER")
                password = project.properties["UNNAMED_REPO_PASSWORD"] as String?
                    ?: System.getenv("REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}