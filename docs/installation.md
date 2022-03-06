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
    implementation("team.unnamed:hephaestus-api:0.1.0")
    
    // Blockbench model reader (optional)
    implementation("team.unnamed:hephaestus-reader-blockbench:0.1.0")
    
    // Bukkit runtime projects
    implementation("team.unnamed:hephaestus-runtime-bukkit-api:0.1.0")
    implementation("team.unnamed:hephaestus-runtime-bukkit-adapt-v1_18_R2:0.1.0")
    
    // Minestom runtime
    implementation("team.unnamed:hephaestus-runtime-minestom:0.1.0")
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
    <version>0.1.0</version>
</dependency>
```