package team.unnamed.hephaestus.adapt.v1_16_R3;

import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelComponent;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Objects;

class ModelViewRenderer_v1_16_R3
        implements ModelViewRenderer {

    private final ModelViewController controller;

    public ModelViewRenderer_v1_16_R3() {
        this.controller = null; // todo
    }

    @Override
    public ModelView render(Player viewer, Model model, Location location) {
        ModelView view = new ModelView(controller, model, viewer, location);
        for (ModelBone bone : model.getGeometry().getBones()) {
            summonBone(view, location, bone, Vector3Float.ZERO);
        }
        return null;
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
        EntityArmorStand entity = new EntityArmorStand(
                worldServer,
                location.getX() + relativePos.getX(),
                location.getY() + relativePos.getY(),
                location.getZ() + relativePos.getZ()
        );

        entity.setSilent(true);
        entity.setNoGravity(true);
        entity.setSmall(true);
        entity.setInvisible(true);

        /*
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(bone.getCustomModelData());
            item.setItemMeta(meta);
        }

        EntityEquipment equipment = stand.getEquipment();

        if (equipment != null) {
            equipment.setHelmet(item);
        }*/

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
