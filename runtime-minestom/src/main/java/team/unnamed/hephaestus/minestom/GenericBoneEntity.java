package team.unnamed.hephaestus.minestom;

import net.minestom.server.color.Color;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.BaseBoneView;

public abstract class GenericBoneEntity extends EntityCreature implements BaseBoneView {

    public GenericBoneEntity(@NotNull EntityType entityType) {
        super(entityType);
    }

    abstract void colorize(Color color);

}