## Model Reading

To create a `Model` instance, we provide the concept of `ModelReader`and
some built-in implementations like the `bbmodel` reader, which reads models
from the [Blockbench](https://blockbench.net/) `bbmodel` format

Every model reader instance has a `ModelDataCursor` instance used to assign
a `custom_model_data` property to every model bones. A single instance of
`ModelDataCursor` should always be used for a resource pack, thus maintaining
unique `custom_model_data` properties for each bone


### BBModel Reader

Create a `ModelReader` for Blockbench models

```java
ModelReader reader = BBModelReader.blockbench();
```

Create a `ModelReader` for Blockbench models, using an assigned `ModelDataCursor`

```java
ModelDataCursor modelDataCursor = new ModelDataCursor(0);
ModelReader reader = BBModelReader.blockbench(modelDataCursor);
```


### Examples

Reading a Blockbench model from a file

```java
ModelReader reader = BBModelReader.blockbench();
File file = new File("model.bbmodel");
Model model;

try (InputStream input = new FileInputStream(file)) {
    model = reader.read(input);
} catch (IOException e) {
    throw new RuntimeException("Cannot read blockbench model", e);
}

// do something with 'model'
```
