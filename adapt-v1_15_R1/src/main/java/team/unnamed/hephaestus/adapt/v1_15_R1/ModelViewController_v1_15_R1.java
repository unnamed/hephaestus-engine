package team.unnamed.hephaestus.adapt.v1_15_R1;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

public class ModelViewController_v1_15_R1
        implements ModelViewController {

    private void summonBone(
            ModelView view,
            Location location,
            ModelBone bone,
            Vector3Float offset
    ) {
        World world = location.getWorld();

        // location computing
        Vector3Float relativePos = Vectors.rotateAroundY(
                bone.getOffset()
                        .multiply(1, 1, -1)
                        .add(offset),
                Math.toRadians(location.getYaw())
        );

        // spawning the bone armorstand
        // noinspection ConstantConditions
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

        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        // noinspection ConstantConditions
        meta.setColor(Color.WHITE);
        meta.setCustomModelData(bone.getCustomModelData());
        item.setItemMeta(meta);

        net.minecraft.server.v1_15_R1.ItemStack nmsItem =
                CraftItemStack.asNMSCopy(item);

        entity.setSlot(EnumItemSlot.HEAD, nmsItem);

        Packets.send(
                view.getViewer(),
                new PacketPlayOutSpawnEntityLiving(entity),
                new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true),
                new PacketPlayOutEntityEquipment(
                        entity.getId(),
                        EnumItemSlot.HEAD,
                        nmsItem
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

        Packets.send(
                view.getViewer(),
                new PacketPlayOutEntityTeleport(entity)
        );

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

    private void colorizeBone(ModelView view, ModelBone bone, Color color) {
        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());

        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        //noinspection ConstantConditions
        meta.setCustomModelData(bone.getCustomModelData());
        meta.setColor(color);
        item.setItemMeta(meta);

        net.minecraft.server.v1_15_R1.ItemStack nmsItem =
                CraftItemStack.asNMSCopy(item);
        entity.setSlot(EnumItemSlot.HEAD, nmsItem);

        Packets.send(
                view.getViewer(),
                new PacketPlayOutEntityEquipment(
                        entity.getId(),
                        EnumItemSlot.HEAD,
                        nmsItem
                )
        );

        for (ModelBone child : bone.getBones()) {
            colorizeBone(view, child, color);
        }
    }

    @Override
    public void colorize(ModelView view, Color color) {
        for (ModelBone bone : view.getModel().getGeometry().getBones()) {
            colorizeBone(view, bone, color);
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
    public void updateBoneModelData(ModelView view, ModelBone bone, int modelData) {

        EntityArmorStand entity = (EntityArmorStand) view.getEntities().get(bone.getName());
        net.minecraft.server.v1_15_R1.ItemStack nmsItem
                = entity.getEquipment(EnumItemSlot.HEAD);

        ItemStack item = nmsItem == null ? new ItemStack(Material.LEATHER_HORSE_ARMOR) : CraftItemStack.asBukkitCopy(nmsItem);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        //noinspection ConstantConditions
        meta.setCustomModelData(modelData);
        if (nmsItem == null) {
            meta.setColor(Color.WHITE);
        }
        item.setItemMeta(meta);

        nmsItem = CraftItemStack.asNMSCopy(item);
        entity.setSlot(EnumItemSlot.HEAD, nmsItem);

        Packets.send(
                view.getViewer(),
                new PacketPlayOutEntityEquipment(
                        entity.getId(),
                        EnumItemSlot.HEAD,
                        nmsItem
                )
        );
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