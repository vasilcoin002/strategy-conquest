package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

public interface IGridEntity {
    Tile getTile();
    void setTile(Tile tile);
    boolean isPassable();
}