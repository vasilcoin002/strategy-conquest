package pjvsemproj.models.entities;

public abstract class Entity implements Positionable {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected final boolean isPassable;

    protected Entity(boolean isPassable) {
        this.isPassable = isPassable;
    }

    public void setX(int x) {
        this.x = x;
    }


}
