package pjvsemproj.models.entities;

/**
 * Represents an entity capable of moving on the map.
 */
public interface Movable extends IGridEntity {

    /**
     * Retrieves the maximum number of tiles this entity can traverse in a single turn.
     *
     * @return the movement range limit
     */
    int getMovementRange();

    /**
     * Checks if the entity has already utilized its movement action during the current turn.
     *
     * @return {@code true} if the entity has moved
     */
    boolean hasMovedThisTurn();

    /**
     * Updates the movement exhaustion state of the entity for the current turn.
     *
     * @param moved {@code true} to mark movement as exhausted, {@code false} to reset
     */
    void setHasMovedThisTurn(boolean moved);
}