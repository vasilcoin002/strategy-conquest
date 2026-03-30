package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

public abstract class Entity implements IGridEntity {
    protected Tile tile;
    protected final boolean isPassable;

    protected Entity(Tile intialTile, boolean isPassable) {
        this.tile = intialTile;
        this.isPassable = isPassable;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    @Override
    public boolean isPassable() {
        return isPassable;
    }
}
