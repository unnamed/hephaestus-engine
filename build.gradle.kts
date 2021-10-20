subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    tasks.withType<JavaCompile> {
        sourceCompatibility = "8"
        targetCompatibility = "8"
    }

    repositories {
        mavenLocal()
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        mavenCentral()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

}
