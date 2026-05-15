package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

/**
 * Represents an object that can be placed on a map tile.
 */
public interface IGridEntity {

    /**
     * Retrieves the specific tile this entity currently occupies.
     *
     * @return the current tile, or {@code null} if unplaced
     */
    Tile getTile();

    /**
     * Updates the entity's spatial location to a new tile.
     *
     * @param tile the new map tile to occupy
     */
    void setTile(Tile tile);

    /**
     * Indicates whether other units can move through the tile occupied by this entity.
     *
     * @return {@code true} if the entity does not block movement
     */
    boolean isPassable();

    /**
     * Retrieves the unique identifier for this entity instance.
     *
     * @return the unique ID string
     */
    String getId();
}