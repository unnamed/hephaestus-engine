subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
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
