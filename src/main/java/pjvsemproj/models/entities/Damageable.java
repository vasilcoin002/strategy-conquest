package pjvsemproj.models.entities;

/**
 * Describes an object that can receive damage and healing.
 */
public interface Damageable {

    /**
     * Reduces the entity's health by the specified amount.
     * Implementations should ensure health does not drop below zero.
     *
     * @param damage the amount of damage to apply
     */
    void takeDamage(int damage);

    /**
     * Increases the entity's health by the specified amount.
     * Implementations should ensure health does not exceed maximum health.
     *
     * @param heal the amount of healing to apply
     */
    void takeHeal(int heal);

    /**
     * Directly sets the current health of the entity.
     *
     * @param health the exact health value to set
     */
    void setHealth(int health);

    /**
     * Retrieves the current health points of the entity.
     *
     * @return current health
     */
    int getHealth();

    /**
     * Retrieves the maximum possible health points for this entity.
     *
     * @return maximum health capacity
     */
    int getMaxHealth();

    /**
     * Determines if the entity has been destroyed or killed.
     *
     * @return {@code true} if health is 0 or below, otherwise {@code false}
     */
    boolean isDead();
}