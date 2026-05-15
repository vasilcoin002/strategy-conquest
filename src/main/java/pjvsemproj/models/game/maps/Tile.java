package pjvsemproj.models.game.maps;

import pjvsemproj.models.entities.IGridEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single map tile.
 * <p>
 * A tile stores its coordinates and all entities currently placed on it.
 */
public class Tile {
    private final int x;
    private final int y;
    private final List<IGridEntity> entities;

    /**
     * Constructs a new empty tile at the specified coordinates.
     *
     * @param x the X coordinate (column index)
     * @param y the Y coordinate (row index)
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.entities = new ArrayList<>();
    }

    /**
     * Attempts to place an entity on this tile.
     * <p>
     * An entity can only be added if it is passable (e.g., a visual effect)
     * OR if the tile is not currently blocked by another impassable entity.
     *
     * @param entity the entity to add
     * @return {@code true} if the entity was successfully added, {@code false} if placement was blocked
     */
    public boolean addEntity(IGridEntity entity) {
        boolean isAdded = false;
        if (entity.isPassable() || !this.isBlocked()) {
            isAdded = this.entities.add(entity);
        }
        return isAdded;
    }

    /**
     * Removes a specific entity from this tile.
     *
     * @param entity the entity to remove
     * @return {@code true} if the entity was found and removed, otherwise {@code false}
     */
    public boolean removeEntity(IGridEntity entity) {
        return this.entities.remove(entity);
    }

    /**
     * Checks if this tile currently contains any entity that blocks movement.
     *
     * @return {@code true} if an impassable entity occupies this tile
     */
    public boolean isBlocked() {
        return !entities.stream().allMatch(IGridEntity::isPassable);
    }

    /**
     * Retrieves the X coordinate of this tile.
     *
     * @return the X index
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y coordinate of this tile.
     *
     * @return the Y index
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves a list of all entities currently occupying this tile.
     *
     * @return the list of entities
     */
    public List<IGridEntity> getEntities() {
        return entities;
    }

    /**
     * Compares this tile to another object.
     * Tiles are considered equal if they share the exact same X and Y coordinates.
     *
     * @param o the object to compare against
     * @return {@code true} if coordinates match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        // The tiles are "equal" if their coordinates match
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}