plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("hephaestus.publishing-conventions")
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

repositories {
    maven("https://libraries.minecraft.net/")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    implementation(project(":hephaestus-runtime-bukkit-api"))
}

tasks {
    reobfJar {
        outputJar = file("build/libs/hephaestus-runtime-bukkit-adapt-v1_20_R3-reobf.jar")
    }
    create<Sign>("signReobfJar") {
        dependsOn(reobfJar)
        description = "Signs the reobfuscated adapt jar"
        val signature = Signature(
                { reobfJar.get().outputJar.get().asFile },
                { "reobf" },
                this,
                this
        )
        signatures.add(signature)
        outputs.files(signature.file)
    }
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            artifact(tasks.reobfJar) {
                classifier = "reobf"
            }
            // Reobf JAR Signature
            artifact(tasks.named("signReobfJar")) {
                classifier = "reobf"
                extension = "asc"
            }
        }
    }
}