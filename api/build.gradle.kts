dependencies {
    api("org.jetbrains:annotations:22.0.0")
    api("com.google.code.gson:gson:2.8.0")
    api("team.unnamed:creative-api:0.1.9-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks {
    test {
        useJUnitPlatform()
    }
}