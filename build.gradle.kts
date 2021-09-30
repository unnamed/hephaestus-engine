subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    tasks.withType<JavaCompile>() {
        options.release.set(8)
    }

    repositories {
        mavenLocal()
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        mavenCentral()
    }

    /*publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }*/
}
