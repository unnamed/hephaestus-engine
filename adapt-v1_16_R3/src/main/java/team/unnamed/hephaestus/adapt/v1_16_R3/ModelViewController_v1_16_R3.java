package team.unnamed.hephaestus.adapt.v1_16_R3;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.Vector3f;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Collections;
import java.util.Objects;

public class ModelViewController_v1_16_R3
        implements ModelViewController {

    private void summonBone(
            ModelView view,
            Location location,
            ModelBone bone,
            Vector3Float offset
    ) {
        World world = location.getWorld();

        // fuck @Nullable
        Objects.requireNonNull(world);

        // location computing
        Vector3Float relativePos = Vectors.rotateAroundY(
                bone.getOffset()
                        .multiply(1, 1, -1)
                        .add(offset),
                Math.toRadians(location.getYaw())
        );

        // spawning the bone armorstand
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        EntityArmorStand entity = new EntityArmorStand(EntityTypes.ARMOR_STAND, worldServer);

        entity.setLocation(
                location.getX() + relativePos.getX(),
                location.getY() + relativePos.getY(),
                location.getZ() + relativePos.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        entity.setSilent(true);
        entity.setNoGravity(true);
        entity.setSmall(true);
        entity.setInvisible(true);

        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta meta = item.getItemMeta();

        // fuck @Nullable again
        Objects.requireNonNull(meta);

        meta.setCustomModelData(bone.getCustomModelData());
        item.setItemMeta(meta);

        Packets.send(
                view.getViewer(),
                new PacketPlayOutSpawnEntityLiving(entity),
                new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true),
                new PacketPlayOutEntityEquipment(
                        entity.getId(),
                        Collections.singletonList(new Pair<>(
                                EnumItemSlot.HEAD,
                                CraftItemStack.asNMSCopy(item)
                        ))
                )
        );

        view.getEntities().put(bone.getName(), entity);

        for (ModelBone component : bone.getBones()) {
            summonBone(
                    view,
                    location,
                    component,
                    offset.add(bone.getOffset().multiply(1, 1, -1))
            );
        }
    }

    @Override
    public void show(ModelView view) {
        for (ModelBone bone : view.getModel().getGeometry().getBones()) {
            summonBone(view, view.getLocation(), bone, Vector3Float.ZERO);
        }
    }

    private void teleportBonesRecursively(
            double yaw,
            ModelView view,
            Location location,
            ModelBone bone,
            Vector3Float offset
    ) {
        World world = location.getWorld();

        // fuck @Nullable
        Objects.requireNonNull(world);

        // location computing
        Vector3Float position = bone.getOffset().multiply(1, 1, -1).add(offset);
        Vector3Float relativePos = Vectors.rotateAroundY(
                position,
                yaw
        );

        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());
        entity.setLocation(
                location.getX() + relativePos.getX(),
                location.getY() + relativePos.getY(),
                location.getZ() + relativePos.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        if (Math.abs(relativePos.getX()) <= 8
                && Math.abs(relativePos.getY()) <= 8
                && Math.abs(relativePos.getZ()) <= 8) {
            Packets.send(
                    view.getViewer(),
                    new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                            entity.getId(),
                            (short) (relativePos.getX() * 4096),
                            (short) (relativePos.getY() * 4096),
                            (short) (relativePos.getZ() * 4096),
                            false // idk what this does
                    )
            );
        } else {
            Packets.send(
                    view.getViewer(),
                    new PacketPlayOutEntityTeleport(entity)
            );
        }

        for (ModelBone component : bone.getBones()) {
            teleportBonesRecursively(
                    yaw,
                    view,
                    location,
                    component,
                    position
            );
        }
    }

    @Override
    public void teleport(ModelView view, Location location) {
        double yaw = Math.toRadians(location.getYaw());
        for (ModelBone bone : view.getModel().getGeometry().getBones()) {
            teleportBonesRecursively(yaw, view, location, bone, Vector3Float.ZERO);
        }
    }

    private void hideBone(ModelView view, ModelBone bone) {
        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());
        Packets.send(view.getViewer(), new PacketPlayOutEntityDestroy(entity.getId()));
        for (ModelBone component : bone.getBones()) {
            hideBone(view, component);
        }
    }

    @Override
    public void hide(ModelView view) {
        for (ModelBone bone : view.getModel().getGeometry().getBones()) {
            hideBone(view, bone);
        }
    }

    @Override
    public void teleportBone(ModelView view, ModelBone bone, Location location) {
        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());
        entity.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
        Packets.send(view.getViewer(), new PacketPlayOutEntityTeleport(entity));
    }

    @Override
    public void setBonePose(ModelView view, ModelBone bone, EulerAngle angle) {
        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());
        DataWatcher watcher = new DataWatcher(null);
        watcher.register(
                new DataWatcherObject<>(15, DataWatcherRegistry.k),
                new Vector3f(
                        (float) Math.toDegrees(angle.getX()),
                        (float) Math.toDegrees(angle.getY()),
                        (float) Math.toDegrees(angle.getZ())
                )
        );
        Packets.send(view.getViewer(), new PacketPlayOutEntityMetadata(entity.getId(), watcher, true));
    }

}
