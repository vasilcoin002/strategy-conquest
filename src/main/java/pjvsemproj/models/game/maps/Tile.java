package pjvsemproj.models.game.maps;

import pjvsemproj.models.entities.IGridEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single map tile.
 *
 * A tile stores its coordinates and all entities currently placed on it.
 */

public class Tile {
    private final int x;
    private final int y;
    private final List<IGridEntity> entities;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.entities = new ArrayList<>();
    }

    public boolean addEntity(IGridEntity entity) {
        boolean isAdded = false;
        if (entity.isPassable() || !this.isBlocked()) {
            isAdded = this.entities.add(entity);
        }
        return isAdded;
    }

    public boolean removeEntity(IGridEntity entity) {
        return this.entities.remove(entity);
    }

    public boolean isBlocked() {
        return !entities.stream().allMatch(IGridEntity::isPassable);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<IGridEntity> getEntities() {
        return entities;
    }
}
