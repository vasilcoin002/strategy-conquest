package pjvsemproj.models.entities;


/**
 * Represents an entity capable of moving on the map.
 */
public interface Movable extends IGridEntity {
    int getMovementRange();
    boolean hasMovedThisTurn();
    void setHasMovedThisTurn(boolean moved);
}