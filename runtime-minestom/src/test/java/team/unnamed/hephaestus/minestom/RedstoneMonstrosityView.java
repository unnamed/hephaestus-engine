package team.unnamed.hephaestus.minestom;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.goal.FollowTargetGoal;

import java.time.Duration;
import java.util.List;

public class RedstoneMonstrosityView
        extends MinestomModelView {

    public RedstoneMonstrosityView() {
        super(EntityType.SLIME, Models.REDSTONE_MONSTROSITY);
        addAIGroup(
                List.of(
                        new FollowTargetGoal(this, Duration.ofSeconds(2))
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
