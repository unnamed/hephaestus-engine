package team.unnamed.hephaestus.adapt.v1_16_R3;

import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.Vector3f;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewController;

public class ModelViewController_v1_16_R3
        implements ModelViewController {

    @Override
    public void teleport(ModelView view, Location location) {

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
