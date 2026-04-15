package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

public interface IGridEntity {
    String getId();
    Tile getTile();
    void setTile(Tile tile);
    boolean isPassable();
}