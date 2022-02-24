## Model Views

`api` defines an interface `ModelView`, which can be used for basic and
platform independent operations such as animations, colors, movements,
animation ticking, etc.

To get a more specialized `ModelView`, you must use the platform specific
implementations


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