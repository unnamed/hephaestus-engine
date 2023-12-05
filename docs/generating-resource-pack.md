## Generating the Resource Pack

Hephaestus-engine uses [creative](https://github.com/unnamed/creative) to
interact with resource pack data, we convert a `Model` and its bones to
creative's item models 

See [creative documentation](https://github.com/unnamed/creative/) for
resource-pack hosting/serving


### Writing to a resource pack

```java
Collection<Model> models = ...;

// creative's ResourcePack
ResourcePack resourcePack = ...;

// write our models to the resource-pack using the "mynamespace" namespace
ModelWriter.resource("mynamespace").write(tree, models);
```


### Writing the resource-pack to a file or directory

This is more detailed on [creative's documentation](https://unnamed.team/docs/creative/)!
So please refer to it for more information.

```java
ResourcePack resourcePack = ...;

// write the resource-pack to a ZIP file...
MinecraftResourcePackWriter.minecraft().writeToZipFile(new File("resource-pack.zip"), resourcePack);

// or write it to a directory...
MinecraftResourcePackWriter.minecraft().writeToDirectory(new File("resource-pack"), resourcePack);
```


### Removing unused information

After we generate the resource pack, some loaded information will be unnecessary,
to solve this, we can use `Model#discardResourcePackData()`, it removes data
like textures, elements, etc. but makes the model unable to be written to a
resource pack *(writer will throw an exception)*