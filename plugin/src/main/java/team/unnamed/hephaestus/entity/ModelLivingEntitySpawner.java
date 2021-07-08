package team.unnamed.hephaestus.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelComponent;
import team.unnamed.hephaestus.model.entity.ModelEntitySpawner;
import team.unnamed.hephaestus.model.entity.ModelLivingEntity;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.UUID;

public class ModelLivingEntitySpawner implements ModelEntitySpawner {

    @Override
    public ModelLivingEntity spawn(Model model, Location location) {
        ModelLivingEntity entity = new ModelLivingEntity(
                model,
                location,
                UUID.randomUUID()
        );
        for (ModelBone bone : model.getGeometry().getBones()) {
            summonBone(entity, location, bone, Vector3Float.ZERO);
        }
        return entity;
    }

    private void summonBone(ModelLivingEntity entity, Location location, ModelBone bone, Vector3Float offset) {
        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Invalid location was given. It doesn't have a world!");
        }

        Vector3Float relativePos = Vectors.rotateAroundY(bone.getLocalOffset()
                .multiply(1, 1, -1)
                .add(offset), Math.toRadians(location.getYaw()));

        Location position = location.clone().add(
                relativePos.getX(),
                relativePos.getY(),
                relativePos.getZ()
        );

        ArmorStand stand = world.spawn(position, ArmorStand.class);
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(bone.getCustomModelData());
            item.setItemMeta(meta);
        }

        EntityEquipment equipment = stand.getEquipment();

        if (equipment != null) {
            equipment.setHelmet(item);
        }

        stand.setSilent(true);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setInvisible(true);

        entity.getEntities().put(
                bone.getName(),
                stand.getUniqueId()
        );

        for (ModelComponent component : bone.getComponents()) {
            if (component instanceof ModelBone) {
                summonBone(entity, location, (ModelBone) component, offset.add(bone.getLocalOffset().multiply(1, 1, -1)));
            }
        }
    }
}