package pjvsemproj.dto;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Container representing a single tile on the map.
 */
public class TileDTO {
    public final int x;
    public final int y;
    public final List<EntityDTO> entities;

    public TileDTO(int x, int y, List<EntityDTO> entities) {
        this.x = x;
        this.y = y;
        this.entities = entities;
    }

    public TileDTO(Tile tile) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.entities = tile.getEntities().stream()
                .map(this::createSpecificDTO)
                .toList();
    }

    public EntityDTO createSpecificDTO(IGridEntity entity) {
        if (entity instanceof City city) {
            return new CityDTO(city);
        } else if (entity instanceof TroopUnit troop) {
            return new TroopUnitDTO(troop);
        }
        // Fallback for generic entities (if you add obstacles/mountains later)
        return new EntityDTO(entity.getId(), "Unknown", entity.getTile().getX(), entity.getTile().getY(), "None");
    }
}
