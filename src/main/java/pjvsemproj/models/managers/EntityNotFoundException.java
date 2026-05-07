package pjvsemproj.models.managers;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String entityId) {
        super(entityName + " with id " + entityId + " not found");
    }
}
