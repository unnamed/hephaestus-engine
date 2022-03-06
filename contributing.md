## Contributing

Hey! Thank you for being interested in contributing, check the following sections
for relevant information to contribute to the development of hephaestus-engine


### Building

Execute the following commands to build the library

```shell
# Clone the repository
git clone https://github.com/unnamed/hephaestus-engine
cd hephaestus-engine

# Build it (use 'shadowJar' if you are going to use 
# runtime-bukkit/test-plugin)
./gradlew build

# Optionally install the projects into your local
# Maven repository
./gradlew publishToMavenLocal
```


### Tests

Remember to run the entire test suite and make sure all tests pass before opening a
pull request, this can be done by running:

```shell
./gradlew test
```

If you are adding a new feature, please add unit tests
