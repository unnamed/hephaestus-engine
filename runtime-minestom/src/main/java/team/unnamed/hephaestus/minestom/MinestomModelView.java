package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.AnimationQueue;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.ModelInteractListener;
import team.unnamed.hephaestus.view.ModelView;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class MinestomModelView
        extends EntityCreature
        implements ModelView<Player> {

    private static final float ARMORSTAND_HEIGHT = 0.726F;

    private final Map<String, BoneEntity> bones = new ConcurrentHashMap<>();

    private final Model model;
    private final AnimationQueue animationQueue;
    private ModelInteractListener<Player> interactListener = ModelInteractListener.nop();

    public MinestomModelView(
            EntityType type,
            Model model
    ) {
        super(type);
        this.model = model;
        this.animationQueue = new AnimationQueue(this);

        setInvisible(true);
        setNoGravity(true);
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public void interactListener(ModelInteractListener<Player> interactListener) {
        this.interactListener = requireNonNull(interactListener, "interactListener");
    }

    ModelInteractListener<Player> interactListener() {
        return interactListener;
    }

    //#region Entire View Handling methods
    @Override
    public void colorize(int r, int g, int b) {
        Color color = new Color(r, g, b);
        for (BoneEntity entity : bones.values()) {
            entity.colorize(color);
        }
    }
    //#endregion

    //#region Bone Handling methods
    @Override
    public void colorizeBone(String name, int r, int g, int b) {
        BoneEntity entity = bones.get(name);
        entity.colorize(new Color(r, g, b));
    }

    @Override
    public void moveBone(String name, Vector3Float position) {
        bones.get(name).teleport(getPosition().add(
                position.x(),
                position.y() - ARMORSTAND_HEIGHT,
                position.z()
        ));
    }

    @Override
    public void rotateBone(String name, Vector3Float rotation) {
        Entity entity = bones.get(name);
        if (entity != null) {
            ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();
            meta.setHeadRotation(new Vec(
                    Math.toDegrees(rotation.x()),
                    Math.toDegrees(rotation.y()),
                    Math.toDegrees(rotation.z())
            ));
        }
    }
    //#endregion

    //#region Animation Handling methods
    @Override
    public void playAnimation(String animationName, int transitionTicks) {
        ModelAnimation animation = model.animations().get(animationName);
        animationQueue.pushAnimation(animation, transitionTicks);
    }

    @Override
    public void playAnimation(ModelAnimation animation, int transitionTicks) {
        animationQueue.pushAnimation(animation, transitionTicks);
    }

    @Override
    public boolean stopAnimation(String name) {
        // TODO:
        return false;
    }

    @Override
    public void stopAllAnimations() {
        animationQueue.removeAllAnimations();
    }

    @Override
    public void tickAnimations() {
        animationQueue.next(Math.toRadians(getPosition().yaw()));
    }
    //#endregion

    private void summonBone(double yawRadians, Pos pos, Bone bone, Vector3Float parentOffset) {

        Vector3Float offset = bone.offset().add(parentOffset);
        Vector3Float relativePos = Vectors.rotateAroundY(offset, yawRadians);

        BoneEntity entity = new BoneEntity(this, bone);

        // todo: maybe we can just show the bones using addViewer
        entity.setInstance(instance, pos.add(
                relativePos.x(),
                relativePos.y(),
                relativePos.z()
        )).join();

        bones.put(bone.name(), entity);

        for (Bone child : bone.bones()) {
            summonBone(yawRadians, pos, child, offset);
        }
    }

    private void teleportBone(
            double yawRadians,
            Pos pos,
            Bone bone,
            Vector3Float parentOffset
    ) {
        Vector3Float offset = bone.offset().add(parentOffset);
        Vector3Float relativePosition = Vectors.rotateAroundY(offset, yawRadians);
        Entity entity = bones.get(bone.name());

        if (entity != null) {
            entity.teleport(pos.add(
                    relativePosition.x(),
                    relativePosition.y(),
                    relativePosition.z()
            ));
        }
        for (Bone child : bone.bones()) {
            this.teleportBone(yawRadians, pos, child, offset);
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    // create the bone entities
                    Pos basePos = spawnPosition.sub(0, ARMORSTAND_HEIGHT, 0);
                    double yawRadians = Math.toRadians(basePos.yaw());

                    for (Bone bone : model.bones()) {
                        summonBone(yawRadians, basePos, bone, Vector3Float.ZERO);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position)
                .thenRun(() -> {
                    Pos basePos = position.sub(0, ARMORSTAND_HEIGHT, 0);
                    double yawRadians = Math.toRadians(basePos.yaw());

                    for (Bone bone : model.bones()) {
                        teleportBone(yawRadians, basePos, bone, Vector3Float.ZERO);
                    }
                });
    }

}