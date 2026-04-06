package pjvsemproj.models.entities;

public interface Damageable {
    void takeDamage(int damage);
    void takeHeal(int heal);
    void setHealth(int health);
    int getHealth();
    int getMaxHealth();
    boolean isDead();
}
