package pjvsemproj.models.entities;

public interface Movable {
    void moveUp(int step);
    void moveDown(int step);
    void moveLeft(int step);
    void moveRight(int step);

    int getStamina();
    void refreshStamina();
    // TODO add method that returns list of available points to move on
}
