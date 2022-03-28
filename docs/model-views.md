## Model Views

`api` defines an interface `BaseModelView`, which can be used for basic
and platform independent operations such as animations, colors, movements,
animation ticking, etc.

To get a more specialized model view, you can use the platform specific
implementations


### Creating Views

Creating a model view depends on the platform


#### Bukkit

To use hephaestus-engine in Bukkit, you will have to include the Runtime
Bukkit API `hephaestus-runtime-bukkit-api` and some implementation like 
`hephaestus-runtime-bukkit-adapt-v1_18_R2` for Paper 1.18.2

```java
// engine can be reused
ModelEngine engine = ModelEngine_v1_18_R2.create();

Model model = ...;
Location location = new Location(world, 0, 60, 0);

ModelEntity view = engine.render(model, location);
```


#### Minestom
TODO!


### Coloring

Model views can be colored with any RGB color, just use `BaseModelView#colorize`
or `BaseBoneView#colorize` to colorize a specific bone

Example:

```java
public void setRedColor(BaseModelView view) {
    view.colorize(255, 0, 0);    
}
```

<!--
### Animation

Animations require the programmer to tick animations in every model view
using `ModelView#tickAnimations`

Example on Bukkit:

```java
Bukkit.getScheduler().runTaskTimerAsynchronously(() -> {
    for (ModelView view : views) {
        view.tickAnimations();    
    }
}, 0L, 1L);
```

Then you can animate views by just using `AnimationController#queue`, stop
animations using `AnimationController#clearQueue`

Example:

```java
public void animate(ModelView<?> view) {
    // walkAnimation is a ModelAnimation instance that can
    // be obtained from Model#animations()
    view.animationController().queue(walkAnimation);
}
```
-->