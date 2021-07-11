package team.unnamed.hephaestus.adapt.v1_16_R3;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelComponent;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Collections;
import java.util.Objects;

public class ModelViewRenderer_v1_16_R3
        implements ModelViewRenderer {

    private final ModelViewController controller;

    public ModelViewRenderer_v1_16_R3() {
        this.controller = new ModelViewController_v1_16_R3();
    }

    @Override
    public ModelView render(Player viewer, Model model, Location location) {
        ModelView view = new ModelView(controller, model, viewer, location);
        for (ModelBone bone : model.getGeometry().getBones()) {
            summonBone(view, location, bone, Vector3Float.ZERO);
        }
        return view;
    }

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
                bone.getLocalOffset()
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

        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.BONE);
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

        for (ModelComponent component : bone.getComponents()) {
            if (component instanceof ModelBone) {
                summonBone(
                        view,
                        location,
                        (ModelBone) component,
                        offset.add(bone.getLocalOffset().multiply(1, 1, -1))
                );
            }
        }
    }

}
