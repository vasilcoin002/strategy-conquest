package pjvsemproj.dto;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

/**
 * Container representing a single entity to be placed on the map.
 */
public class EntityDTO {
    // adding transient to tell Gson to not serialize it
    public final transient String id;
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

//    public EntityDTO(IGridEntity entity) {
//        // initializing default value because entityType is final
//        String name = "Entity";
//        if (entity instanceof City) {
//            name = "City";
//        }
//        else if (entity instanceof TroopUnit troopUnit) {
//            name = troopUnit.getName();
//        }
//        this.entityType = name;
//        this.id = entity.getId();
//        this.x = entity.getTile().getX();
//        this.y = entity.getTile().getY();
//        if (entity instanceof Ownable ownable) {
//            this.ownerName = ownable.getOwner().getName();
//        }
//    }
}
