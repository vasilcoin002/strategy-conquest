package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

import java.util.UUID;


/**
 * Base abstract implementation of an entity placed on the game map.
 *
 * Stores a unique identifier, current tile position, and passability flag.
 * Serves as a common parent for all map entities such as cities and troop units.
 */

public abstract class Entity implements IGridEntity {

    protected final String id;
    protected Tile tile;
    protected final boolean isPassable;

    protected Entity(Tile intialTile, boolean isPassable) {
        this.id = UUID.randomUUID().toString();
        this.tile = intialTile;
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
}
