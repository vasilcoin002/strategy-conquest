package pjvsemproj.dto;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;

import java.util.List;
import java.util.Objects;

/**
 * Container representing a single tile on the map.
 * <p>
 * This Data Transfer Object (DTO) aggregates coordinates representing a specific cell
 * inside the game grid map, alongside a collection of data transfer wrappers for all entities
 * currently occupying that cell.
 */
public class TileDTO {
    public final int x;
    public final int y;
    public final List<EntityDTO> entities;

    /**
     * Constructs a tile data transfer container with an explicit position coordinate and pre-wrapped entities.
     *
     * @param x        The cell's column coordinate index position along the horizontal map grid axis.
     * @param y        The cell's row coordinate index position along the vertical map grid axis.
     * @param entities A list of pre-constructed {@link EntityDTO} nodes located inside this specific grid cell.
     */
    public TileDTO(int x, int y, List<EntityDTO> entities) {
        this.x = x;
        this.y = y;
        this.entities = entities;
    }

    /**
     * Contextual hydration constructor that maps a live structural grid tile directly into a safe data transfer representation.
     * <p>
     * Resolves layout coordinates and streams the inner domain entity collection through the concrete
     * sub-class factory pipeline.
     *
     * @param tile The live active {@link Tile} grid cell instance to extract coordinates and occupant tracking lists from.
     */
    public TileDTO(Tile tile) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.entities = tile.getEntities().stream()
                .map(this::createSpecificDTO)
                .toList();
    }

    /**
     * Core factory polymorphism helper method that parses a generic map entity interface and downcasts it into its concrete DTO equivalent.
     * <p>
     * Evaluates instance-of matches against domain boundaries to initialize a specialized {@link CityDTO}
     * or {@link TroopUnitDTO}, falling back to a base container if no matches are found.
     *
     * @param entity The generic {@link IGridEntity} implementation instance residing on the active map grid.
     * @return A specialized sub-classed {@link EntityDTO} container preserving inner statistical properties.
     */
    public EntityDTO createSpecificDTO(IGridEntity entity) {
        if (entity instanceof City city) {
            return new CityDTO(city);
        } else if (entity instanceof TroopUnit troop) {
            return new TroopUnitDTO(troop);
        }
        // Fallback for generic entities (if you add obstacles/mountains later)
        return new EntityDTO(entity.getId(), "Unknown", entity.getTile().getX(), entity.getTile().getY(), "None");
    }

    /**
     * Compares this tile DTO against a target object to evaluate structural equality.
     * <p>
     * Enforces strict class type checks and returns true only if the horizontal and vertical coordinate
     * indexes match exactly.
     *
     * @param o A generic object reference to compare against this instance.
     * @return {@code true} if the target object is a {@code TileDTO} matching coordinates exactly;
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TileDTO tileDTO = (TileDTO) o;
        return x == tileDTO.x && y == tileDTO.y;
    }

    /**
     * Generates a numeric integer hash value representing this tile data container.
     * <p>
     * Pairs coordinate axes to comply with standard equals contract definitions.
     *
     * @return An integer hash code generated from spatial grid positions.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}