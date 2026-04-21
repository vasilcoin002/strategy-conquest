package pjvsemproj.models.entities;

/**
 * Describes an object that can receive damage and healing.
 */
public interface Damageable {
    void takeDamage(int damage);
    void takeHeal(int heal);
    void setHealth(int health);
    int getHealth();
    int getMaxHealth();
    boolean isDead();
}
