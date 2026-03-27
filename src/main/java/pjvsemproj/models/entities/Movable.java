package pjvsemproj.models.entities;

public interface Movable extends IGridEntity {
    int getMovementRange();
    boolean hasMovedThisTurn();
    void setHasMovedThisTurn(boolean moved);
}