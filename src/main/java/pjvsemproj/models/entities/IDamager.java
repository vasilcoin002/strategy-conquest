package pjvsemproj.models.entities;

/**
 * Represents an entity capable of dealing combat damage.
 */
public interface IDamager {

    /**
     * Computes the actual damage to be dealt in a specific attack instance.
     * Typically evaluates a value between minDamage and maxDamage.
     *
     * @return the calculated damage output
     */
    int calculateDamage();

    /**
     * Retrieves the maximum distance (in tiles) at which this entity can strike a target.
     *
     * @return attack range in tiles
     */
    int getAttackRange();

    /**
     * Checks if the entity has already performed an attack during the current turn.
     *
     * @return {@code true} if an attack has been made this turn
     */
    boolean hasAttackedThisTurn();

    /**
     * Updates the attack state of the entity for the current turn.
     *
     * @param attacked {@code true} to mark the entity as having attacked, {@code false} to reset
     */
    void setHasAttackedThisTurn(boolean attacked);

    /**
     * Retrieves the absolute minimum damage this entity can deal.
     *
     * @return minimum damage bound
     */
    int getMinDamage();

    /**
     * Retrieves the absolute maximum damage this entity can potentially deal.
     *
     * @return maximum damage bound
     */
    int getMaxDamage();
}