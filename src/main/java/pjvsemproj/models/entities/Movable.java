package pjvsemproj.models.entities;

public interface Movable {
    public void moveUp();
    public void moveDown();
    public void moveLeft();
    public void moveRight();

    public void moveUp(int step);
    public void moveDown(int step);
    public void moveLeft(int step);
    public void moveRight(int step);
}
