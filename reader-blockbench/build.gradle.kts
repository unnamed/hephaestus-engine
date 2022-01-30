dependencies {
    api(project(":hephaestus-api"))
    api(libs.gson)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks {
    test {
        useJUnitPlatform()
    }
}