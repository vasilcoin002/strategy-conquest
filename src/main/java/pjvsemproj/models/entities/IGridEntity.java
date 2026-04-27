package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

/**
 * Represents an object that can be placed on a map tile.
 */
public interface IGridEntity {
    String getId();
    Tile getTile();
    void setTile(Tile tile);
    boolean isPassable();
    String getId();
}