dependencies {
    api(libs.annotations)
    api(libs.creative)
    api(libs.adventure.api)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks {
    test {
        useJUnitPlatform()
    }
}