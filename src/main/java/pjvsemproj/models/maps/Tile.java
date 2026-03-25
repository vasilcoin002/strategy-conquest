package pjvsemproj.models.maps;

import pjvsemproj.models.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    private final int x;
    private final int y;
    private final List<Entity> entities;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.entities = new ArrayList<>();
    }

    public boolean addEntity(Entity entity) {
        boolean isAdded = false;
        if (entity.isPassable() || !this.isAlreadyTaken()) {
            isAdded = this.entities.add(entity);
        }
        return isAdded;
    }

    public boolean removeEntity(Entity entity) {
        return this.entities.remove(entity);
    }

    public boolean isAlreadyTaken() {
        return !entities.stream().allMatch(Entity::isPassable);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
