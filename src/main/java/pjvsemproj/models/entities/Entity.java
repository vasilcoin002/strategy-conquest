package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

import java.util.Objects;
import java.util.UUID;

/**
 * Base abstract implementation of an entity placed on the game map.
 * <p>
 * Stores a unique identifier, current tile position, and passability flag.
 * Serves as a common parent for all map entities such as cities and troop units.
 */
public abstract class Entity implements IGridEntity {

    protected final String id;
    protected Tile tile;
    protected final boolean isPassable;

    /**
     * Constructs a new entity with an automatically generated unique identifier.
     *
     * @param initialTile the starting tile where the entity is placed
     * @param isPassable  {@code true} if other entities can move through this entity's tile
     */
    protected Entity(Tile initialTile, boolean isPassable) {
        this(UUID.randomUUID().toString(), initialTile, isPassable);
    }

    /**
     * Constructs a new entity with a specific identifier.
     * If the provided identifier is null or blank, a random UUID is generated.
     *
     * @param id          the specific unique identifier for this entity
     * @param initialTile the starting tile where the entity is placed
     * @param isPassable  {@code true} if other entities can move through this entity's tile
     */
    protected Entity(String id, Tile initialTile, boolean isPassable) {
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.tile = initialTile;
        this.isPassable = isPassable;
    }

    /**
     * Returns the tile currently occupied by this entity.
     *
     * @return current tile or {@code null} if not placed
     */
    @Override
    public Tile getTile() {
        return tile;
    }

    /**
     * Updates the tile reference of this entity.
     *
     * @param tile new tile to assign
     */
    @Override
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     * Indicates whether this entity blocks movement.
     *
     * @return {@code true} if the entity is passable
     */
    @Override
    public boolean isPassable() {
        return isPassable;
    }

    /**
     * Returns the unique identifier of this entity.
     *
     * @return entity identifier
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Compares this entity to another object for equality based on their identifiers.
     *
     * @param o the object to compare to
     * @return {@code true} if the objects have the same ID, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}