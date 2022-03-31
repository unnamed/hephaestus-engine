## Generating the Resource Pack

Hephaestus-engine uses [creative](https://github.com/unnamed/creative) to
interact with resource pack data, we convert a `Model` and its bones to
creative's block models 

See [creative documentation](https://github.com/unnamed/creative/) for
resource-pack hosting/serving


### Writing to a resource pack ZIP

```java
Collection<Model> models = ...;

File file = new File("resource-pack.zip");
file.createNewFile();

try (FileTree tree = FileTree.zip(new ZipOutputStream(new FileOutputStream(file)))) {
    ModelWriter.resource("mynamespace").write(tree, models);
    
    // you can write your own assets here!
    // check https://github.com/unnamed/creative
}
```


### Writing to a directory

```java
Collection<Model> models = ...;

File directory = new File("resource-pack");
directory.mkdirs();

try (FileTree tree = FileTree.directory(directory)) {
    ModelWriter.resource("mynamespace").write(tree, models);
    
    // you can write your own assets here!
    // check https://github.com/unnamed/creative
}
```


### Removing unused information

After we generate the resource pack, some loaded information will be unnecessary,
to solve this, we can use `Model#discardResourcePackData()`, it removes data
like textures, elements, etc. but makes the model unable to be written to a
resource pack *(writer will throw an exception)*