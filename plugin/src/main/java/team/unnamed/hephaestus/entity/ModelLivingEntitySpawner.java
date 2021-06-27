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
            summonBone(entity, location, bone);
        }
        return entity;
    }

    private void summonBone(ModelLivingEntity entity, Location location, ModelBone bone) {

        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Invalid location was given. It doesn't have a world!");
        }

        Vector3Float relativePos = new Vector3Float(
                bone.getLocalOffset().getX(),
                bone.getLocalOffset().getY(),
                -bone.getLocalOffset().getZ()
        );

        relativePos = Vectors.rotate(relativePos, -location.getYaw() * 0.017453292F);

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
                summonBone(entity, location, (ModelBone) component);
            }
        }
    }


}
