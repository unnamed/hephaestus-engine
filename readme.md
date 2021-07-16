# Hephaestus Engine
Hephaestus Engine is a library which allows the visualization of block bench models and animations in a Minecraft server by the use of a generated resource pack and armor stands.
# Installation
Latest Snapshot: [![Latest Snapshot](https://img.shields.io/nexus/s/team.unnamed.hephaestus/hephaestus-api.svg?server=https%3A%2F%2Frepo.unnamed.team)](https://repo.unnamed.team/repository/unnamed-snapshots)

Latest Release: [![Latest Release](https://img.shields.io/nexus/r/team.unnamed.hephaestus/hephaestus-api.svg?server=https%3A%2F%2Frepo.unnamed.team)](https://repo.unnamed.team/repository/unnamed-snapshots)
# Maven Dependency
Add the repositories into your  `<repositories>`  tag (`pom.xml`)
```XML
<repository>
  <id>unnamed-public</id>
  <url>https://repo.unnamed.team/repository/unnamed-public/</url>
</repository>
```
Add the dependency into your  `<dependencies>`  tag (`pom.xml`)
```XML
<dependency>
  <groupId>team.unnamed.hephaestus</groupId>
  <artifactId>hephaestus-api</artifactId>
  <version>VERSION</version>
</dependency>
```
# Contents
- [API](https://github.com/unnamed/hephaestus-engine/tree/master/api)
- [Molang Parser](https://github.com/unnamed/hephaestus-engine/tree/master/molang)
- [Plugin](https://github.com/unnamed/hephaestus-engine/tree/master/plugin)
# Limitations
- Cube rotations are limited by increments of 22.5 from -45 to 45
- Bones cannot be larger than 48x48x48
# Recommended format
Hephaestus engine allows for flexibility while modeling and handling animations but there is a recommended format to follow when modeling. You should start with a root bone and have the rest of the model as it's descendant. Here's an example on how our iconic pegasus model is made:

![Pegasus](pegasus-format.png)
# Features
- Deploys Blockbench models into game-ready texture packs
- Plays animations
- Parses molang in keyframes *(TODO)*
# Credits
- [BiConsumer](https://github.com/BiConsumer)
- [Yusshu](https://github.com/yusshu)
- [Neerixx](https://github.com/Neerixx)
# Known bugs or misbehaviours
The next list are the known bugs that must be resolved at some point:
- Animations do not transition in between eachother
- Animations are not able to overlap each other