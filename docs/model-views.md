## Model Views

`api` defines an interface `ModelView`, which can be used for basic and
platform independent operations such as animations, colors, movements,
animation ticking, etc.

To get a more specialized model view, you can use the platform specific
implementations (i.e. BukkitModelView, MinestomModelView)


### Creating Views

Creating a model view depends on the platform


#### Bukkit

To use hephaestus-engine in Bukkit, you will have to include the Runtime
Bukkit API `hephaestus-runtime-bukkit-api` and some implementation like 
`hephaestus-runtime-bukkit-adapt-v1_18_R2` for Spigot 1.18.2

```java
// renderer can be reused
ModelViewRenderer renderer = new ModelViewRenderer_v1_18_R2();

Model model = ...;
Location location = new Location(world, 0, 60, 0);

// note that BukkitModelView is the specialized ModelView
// implementation for Bukkit
BukkitModelView view = renderer.render(model, location);

// show the view to a player
view.addViewer(player);

// hide the view from a player
view.removeViewer(player);

// IMPORTANT: hephaestus-engine does not manage vision range
// so you will have to manually add and remove viewers to/from
// a view
```


#### Minestom
TODO!


### Coloring

Model views can be colored with any RGB color, just use `ModelView#colorize`
or `BoneView#colorize` to colorize a specific bone

Example:

```java
public void setRedColor(ModelView<?> view) {
    view.colorize(255, 0, 0);    
}
```


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