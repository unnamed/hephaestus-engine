package team.unnamed.hephaestus.model.view;

import team.unnamed.hephaestus.model.animation.ModelAnimation;

/**
 * Responsible of animating model
 * living entities
 */
public interface ModelEntityAnimator {

    /**
     * Starts a task for animating the given
     * {@code entity} with the specified {@code animation}.
     * @return The created task id
     */
    int animate(ModelView entity, ModelAnimation animation);

}
