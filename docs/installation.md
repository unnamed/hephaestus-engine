## Installation

You can add hephaestus-engine to your project using [Gradle](https://gradle.org/)
*(recommended)*, [Maven](https://maven.apache.org/) or manually downloading the
JAR files


### Gradle

Add our repository

```kotlin
repositories {
    maven("https://repo.unnamed.team/repository/unnamed-public/")
}
```

Add the necessary dependencies

```kotlin
dependencies {
    // Core API, necessary for everything
    implementation("team.unnamed:hephaestus-api:0.1.0-SNAPSHOT")
    
    // Blockbench model reader (optional)
    implementation("team.unnamed:hephaestus-reader-blockbench:0.1.0-SNAPSHOT")
    
    // Bukkit runtime projects (you might want to add :reobf classifier to adapt-v1_20_R3)
    implementation("team.unnamed:hephaestus-runtime-bukkit-api:0.1.0-SNAPSHOT")
    implementation("team.unnamed:hephaestus-runtime-bukkit-adapt-v1_20_R3:0.1.0-SNAPSHOT")
    
    // Minestom runtime
    implementation("team.unnamed:hephaestus-runtime-minestom:0.1.0-SNAPSHOT")
}
```

### Maven

Add our repository

```xml
<repository>
    <id>unnamed-public</id>
    <url>https://repo.unnamed.team/repository/unnamed-public/</url>
</repository>
```

Add the necessary dependencies

```xml
<dependency>
    <groupId>team.unnamed</groupId>
    <artifactId>hephaestus-api</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```