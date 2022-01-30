dependencies {
    api(libs.annotations)
    api(libs.creative)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks {
    test {
        useJUnitPlatform()
    }
}