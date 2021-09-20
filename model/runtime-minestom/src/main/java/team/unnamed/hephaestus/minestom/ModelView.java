package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class ModelView extends LivingEntity {

    private final Map<String, Entity> bones = new HashMap<>();

    private final Model model;
    private final ModelViewAnimator animator;
    private final ModelAnimationQueue animationQueue;

    public ModelView(
            EntityType type,
            Model model,
            ModelViewAnimator animator,
            ModelAnimationQueue animationQueue
    ) {
        super(type);
        this.model = model;
        this.animator = animator;
        this.animationQueue = animationQueue;

        setInvisible(true);
        setNoGravity(true);
    }

    public Model getModel() {
        return model;
    }

    public ModelAnimationQueue getAnimationQueue() {
        return animationQueue;
    }

    public void animate(String animationName) {
        ModelAnimation animation = model.getAnimations().get(animationName);
        Check.notNull(animation, "Unknown animation");

        animator.animate(this);
    }

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

    public void teleportBone(ModelBone bone, Pos position) {
        Entity entity = bones.get(bone.getName());
        if (entity != null) {
            entity.teleport(position);
        }
    }

    public void setBonePose(ModelBone bone, Vector3Double pose) {
        Entity entity = bones.get(bone.getName());
        if (entity != null) {
            ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();
            meta.setHeadRotation(new Vec(
                    Math.toDegrees(pose.getX()),
                    Math.toDegrees(pose.getY()),
                    Math.toDegrees(pose.getZ())
            ));
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    double yaw = Math.toRadians(spawnPosition.yaw());
                    for (ModelBone bone : model.getBones()) {
                        summonBone(yaw, spawnPosition, bone, Vector3Float.ZERO);
                    }
                });
    }

}