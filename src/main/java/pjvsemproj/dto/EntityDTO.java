package pjvsemproj.dto;

/**
 * A container representing a single entity to be placed on the map.
 */
public class EntityDTO {
    public final String id;
    public final String entityType; // e.g., "CITY", "INFANTRY", "CAVALRY"
    public final int x;
    public final int y;
    public final String ownerName;

    public EntityDTO(String id, String entityType, int x, int y, String ownerName) {
        this.id = id;
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.ownerName = ownerName;
    }
}
