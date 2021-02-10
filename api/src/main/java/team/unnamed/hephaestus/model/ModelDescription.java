package team.unnamed.hephaestus.model;

/**
 * Class that holds descriptive
 * data about a specific {@link ModelGeometry}
 */
public class ModelDescription {

    private final String identifier;

    public ModelDescription(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
