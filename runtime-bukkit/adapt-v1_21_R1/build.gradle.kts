plugins {
    id("hephaestus.runtime-bukkit-conventions")
    id("hephaestus.publishing-conventions")
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

repositories {
    maven("https://libraries.minecraft.net/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    implementation(project(":hephaestus-runtime-bukkit-api"))
}

tasks {
    reobfJar {
        outputJar = file("build/libs/hephaestus-runtime-bukkit-adapt-v1_21_R1-reobf.jar")
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