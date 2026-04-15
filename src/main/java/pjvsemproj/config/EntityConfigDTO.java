package pjvsemproj.config;

/**
 * A container representing a single entity to be placed on the map.
 */
public class EntityConfigDTO {
    public final String entityType; // e.g., "CITY", "INFANTRY", "CAVALRY"
    public final int x;
    public final int y;
    public final String ownerName; // 1 = Player 1, 2 = Player 2, 0 = Neutral

    public EntityConfigDTO(String entityType, int x, int y, String ownerName) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.ownerName = ownerName;
    }
}
