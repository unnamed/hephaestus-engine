## Model Views

`api` defines an interface `BaseModelView`, which can be used for basic
and platform independent operations such as animations, colors, movements,
animation ticking, etc.

To get a more specialized model view, you can use the platform specific
implementations


### Creating Views

Creating a model view depends on the platform


#### Bukkit
TODO!

#### Minestom
To use hephaestus-engine in Minestom, you will have to include the Runtime
Minestom subproject `hephaestus-runtime-minestom`

```java
Model model = ...;
ModelEntity view = MinestomModelEngine.minestom().createViewAndTrack(
        model,
        instance, // Instance
        position  // Pos
);
```


### Coloring

Model views can be colored with any RGB color, just use `BaseModelView#colorize`
or `BaseBoneView#colorize` to colorize a specific bone

Example:

```java
public void setRedColor(BaseModelView<?> view) {
    view.colorize(255, 0, 0);    
}
```

### Animation

You can animate views by just using `AnimationController#queue`, stop
animations using `AnimationController#clearQueue`

Example:

```java
public void animate(BaseModelView<?> view) {
    // walkAnimation is a ModelAnimation instance that can
    // be obtained from Model#animations()
    view.animationPlayer().queue(walkAnimation);
}
```