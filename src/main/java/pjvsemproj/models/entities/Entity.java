package pjvsemproj.models.entities;

public abstract class Entity implements Positionable {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean isPassable;

    protected int rotation;

    protected int speed;

}
