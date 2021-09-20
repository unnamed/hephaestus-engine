package team.unnamed.hephaestus.minestom;

/**
 * Responsible for animating model
 * views
 */
public interface ModelViewAnimator {

    /**
     * Starts a task for animating the given
     * {@code view} with the specified {@code animation}.
     * @return The created task id
     */
    int animate(ModelView view);

}