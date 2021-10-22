package team.unnamed.hephaestus.minestom;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;

import java.util.List;

public class RedstoneMonstrosityModel
        extends MinestomModelView {

    public RedstoneMonstrosityModel() {
        super(EntityType.SLIME, Models.REDSTONE_MONSTROSITY);
        addAIGroup(
                List.of(
                        new RandomStrollGoal(this, 5)
                ),
                List.of()
        );
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        super.tickAnimations();
    }

}
