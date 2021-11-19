package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.animation.AnimationQueue;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.ModelView;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MinestomModelView
        extends EntityCreature
        implements ModelView {

    private static final float ARMORSTAND_HEIGHT = 0.726F;

    private static final ItemStack BASE_HELMET = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .meta(new LeatherArmorMeta.Builder()
                    .color(new Color(0xFFFFFF))
                    .build())
            .build();

    private final Map<String, LivingEntity> bones = new ConcurrentHashMap<>();

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
                    (LeatherArmorMeta.Builder meta) -> meta.color(color)
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
                (LeatherArmorMeta.Builder meta) -> meta.color(color)
        ));
    }

    @Override
    public void moveBone(String name, Vector3Float position) {
        bones.get(name).teleport(getPosition().add(
                position.getX(),
                position.getY() - ARMORSTAND_HEIGHT,
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
    public void playAnimation(String animationName, int transitionTicks) {
        ModelAnimation animation = model.getAnimations().get(animationName);
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

    private void summonBone(double yawRadians, Pos pos, ModelBone bone, Vector3Float parentOffset) {
        Instance instance = this.instance;

        Vector3Float offset = bone.getOffset().add(parentOffset);

        Vector3Float relativePos = Vectors.rotateAroundY(offset, yawRadians);

        LivingEntity entity = new LivingEntity(EntityType.ARMOR_STAND);
        ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();

        meta.setSilent(true);
        meta.setHasNoGravity(true);
        meta.setSmall(true);
        meta.setInvisible(true);

        entity.setHelmet(BASE_HELMET.withMeta(itemMeta ->
                itemMeta.customModelData(bone.getCustomModelData())));

        // todo: maybe we can just show the bones using addViewer
        entity.setInstance(instance, pos.add(
                relativePos.getX(),
                relativePos.getY(),
                relativePos.getZ()
        )).join();

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
        Vector3Float offset = bone.getOffset().add(parentOffset);
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
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    // create the bone entities
                    Pos basePos = spawnPosition.sub(0, ARMORSTAND_HEIGHT, 0);
                    double yawRadians = Math.toRadians(basePos.yaw());
                    for (ModelBone bone : model.getBones()) {
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

                    for (ModelBone bone : this.getModel().getBones()) {
                        teleportBone(yawRadians, basePos, bone, Vector3Float.ZERO);
                    }
                });
    }

}