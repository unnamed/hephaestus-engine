plugins {
    id("hephaestus.publishing-conventions")
}

dependencies {
    api(project(":hephaestus-api"))
    api(libs.gson)
}