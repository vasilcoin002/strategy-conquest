package pjvsemproj.dto;

/**
 * Container representing a single entity to be placed on the map.
 */
public class EntityDTO {
    public String id;
    public String entityType; // e.g., "CITY", "INFANTRY", "CAVALRY"
    public int x;
    public int y;
    public String ownerName;

    public EntityDTO(String id, String entityType, int x, int y, String ownerName) {
        this.id = id;
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.ownerName = ownerName;
    }
}
