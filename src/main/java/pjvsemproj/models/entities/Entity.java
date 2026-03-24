package pjvsemproj.models.entities;

public abstract class Entity implements Positionable {
    protected int x;
    protected int y;
    protected final boolean isPassable;

    protected Entity(int x, int y, boolean isPassable) {
        this.x = x;
        this.y = y;
        this.isPassable = isPassable;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public boolean isPassable() {
        return isPassable;
    }
}
