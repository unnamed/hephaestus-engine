package team.unnamed.hephaestus.model.view;

/**
 * Responsible of animating model
 * living entities
 */
public interface ModelViewAnimator {

    /**
     * Starts a task for animating the given
     * {@code view} with the specified {@code animation}.
     * @return The created task id
     */
    int animate(ModelView view);

}
