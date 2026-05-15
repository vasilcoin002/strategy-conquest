package pjvsemproj.models.managers;

/**
 * Custom runtime exception thrown when an operation attempts to access an entity
 * that does not exist in the current game state.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs a new EntityNotFoundException with a descriptive error message.
     *
     * @param entityName the type or descriptive name of the entity (e.g., "City", "Troop")
     * @param entityId   the unique identifier that could not be found
     */
    public EntityNotFoundException(String entityName, String entityId) {
        super(entityName + " with id " + entityId + " not found");
    }
}