package pjvsemproj.models.entities;

public interface Damageable {
    public void takeDamage(int damage);
    public void takeHeal(int heal);
    public void setHealth(int health);
}
