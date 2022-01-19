package team.unnamed.hephaestus.minestom;

import net.minestom.server.entity.EntityType;

public class RedstoneMonstrosityView
        extends MinestomModelView {

    public RedstoneMonstrosityView() {
        super(EntityType.SLIME, Models.REDSTONE_MONSTROSITY);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        super.tickAnimations();
    }

}
