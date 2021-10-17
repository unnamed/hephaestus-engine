package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.AnimationQueue;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MinestomModelView
        extends LivingEntity
        implements ModelView {

    private final Map<String, LivingEntity> bones = new HashMap<>();

    private final Model model;
    private final AnimationQueue animationQueue;

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
    public Model getModel() {
        return model;
    }

    //#region Entire View Handling methods
    @Override
    public void colorize(int r, int g, int b) {
        Color color = new Color(r, g, b);
        for (LivingEntity entity : bones.values()) {
            entity.setHelmet(entity.getHelmet().withMeta(
                    LeatherArmorMeta.class,
                    meta -> meta.color(color)
            ));
        }
    }
    //#endregion

    //#region Bone Handling methods
    @Override
    public void colorizeBone(String name, int r, int g, int b) {
        LivingEntity entity = bones.get(name);
        Color color = new Color(r, g, b);
        entity.setHelmet(entity.getHelmet().withMeta(
                LeatherArmorMeta.class,
                meta -> meta.color(color)
        ));
    }

    @Override
    public void moveBone(String name, Vector3Float position) {
        bones.get(name).teleport(getPosition().add(
                position.getX(),
                position.getY(),
                position.getZ()
        ));
    }

    @Override
    public void rotateBone(String name, Vector3Double rotation) {
        Entity entity = bones.get(name);
        if (entity != null) {
            ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();
            meta.setHeadRotation(new Vec(
                    Math.toDegrees(rotation.getX()),
                    Math.toDegrees(rotation.getY()),
                    Math.toDegrees(rotation.getZ())
            ));
        }
    }
    //#endregion

    //#region Animation Handling methods
    @Override
    public void playAnimation(String animationName) {
        ModelAnimation animation = model.getAnimations().get(animationName);
        animationQueue.pushAnimation(animation);
    }

    @Override
    public void playAnimation(ModelAnimation animation) {
        animationQueue.pushAnimation(animation);
    }

    @Override
    public boolean stopAnimation(String name) {
        // TODO:
        return false;
    }

    @Override
    public void stopAllAnimations() {
        // TODO
    }

    @Override
    public void tickAnimations() {
        animationQueue.next(Math.toRadians(getPosition().yaw()));
    }
    //#endregion

    private void summonBone(double yawRadians, Pos pos, ModelBone bone, Vector3Float parentOffset) {
        Instance instance = this.instance;

        Vector3Float offset = bone.getOffset()
                .multiply(1, 1, -1)
                .add(parentOffset);

        Vector3Float relativePos = Vectors.rotateAroundY(offset, yawRadians);

        LivingEntity entity = new LivingEntity(EntityType.ARMOR_STAND);
        ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();

        meta.setSilent(true);
        meta.setHasNoGravity(true);
        meta.setSmall(true);
        meta.setInvisible(true);

        ItemStack helmet = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                .meta(new LeatherArmorMeta.Builder()
                        .color(new Color(0xFFFFFF))
                        .customModelData(bone.getCustomModelData())
                        .build())
                .build();

        entity.setHelmet(helmet);

        // todo: maybe we can just show the bones using addViewer
        entity.setInstance(instance, pos.add(
                relativePos.getX(),
                relativePos.getY(),
                relativePos.getZ()
        ));

        bones.put(bone.getName(), entity);

        for (ModelBone child : bone.getBones()) {
            summonBone(yawRadians, pos, child, offset);
        }
    }

    private void teleportBone(
            double yawRadians,
            Pos pos,
            ModelBone bone,
            Vector3Float parentOffset
    ) {
        Vector3Float offset = bone.getOffset()
                .multiply(1, 1, -1)
                .add(parentOffset);
        Vector3Float relativePosition = Vectors.rotateAroundY(offset, yawRadians);
        Entity entity = bones.get(bone.getName());

        if (entity != null) {
            entity.teleport(pos.add(
                    relativePosition.getX(),
                    relativePosition.getY(),
                    relativePosition.getZ()
            ));
        }
        for (ModelBone child : bone.getBones()) {
            this.teleportBone(yawRadians, pos, child, offset);
        }
    }

    @Override
    protected boolean addViewer0(@NotNull Player player) {
        if (super.addViewer0(player)) {
            bones.forEach((name, entity) -> entity.addViewer(player));
        }
        return false;
    }

    @Override
    protected boolean removeViewer0(@NotNull Player player) {
        if (super.removeViewer0(player)) {
            bones.forEach((name, entity) -> entity.removeViewer(player));
        }
        return false;
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    // create the bone entities
                    double yawRadians = Math.toRadians(spawnPosition.yaw());
                    for (ModelBone bone : model.getBones()) {
                        summonBone(yawRadians, spawnPosition, bone, Vector3Float.ZERO);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position)
                .thenRun(() -> {
                    for (ModelBone bone : this.getModel().getBones()) {
                        teleportBone(Math.toRadians(position.yaw()), position, bone, Vector3Float.ZERO);
                    }
                });
    }

}