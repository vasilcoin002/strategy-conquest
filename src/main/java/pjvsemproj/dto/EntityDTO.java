package pjvsemproj.dto;

/**
 * Container representing a single entity to be placed on the map.
 * <p>
 * Acts as the base Data Transfer Object (DTO) for all elements residing on the game board grid.
 * It wraps shared spatial coordinates, unique identification keys, and ownership state metrics.
 */
public class EntityDTO {
    public String id;
    public String entityType; // e.g., "CITY", "INFANTRY", "CAVALRY"
    public int x;
    public int y;
    public String ownerName;

    /**
     * Constructs a base entity data transfer container with explicit state specifications.
     *
     * @param id         The unique metadata string tracking token assigned to this specific entity asset instance.
     * @param entityType Descriptive label representing the structural class type configuration, e.g., "CITY", "INFANTRY".
     * @param x          The spatial grid coordinate position along the horizontal column map axis.
     * @param y          The spatial grid coordinate position along the vertical row map axis.
     * @param ownerName  The distinct profile username string matching the player who currently controls this entity.
     */
    public EntityDTO(String id, String entityType, int x, int y, String ownerName) {
        this.id = id;
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.ownerName = ownerName;
    }
}