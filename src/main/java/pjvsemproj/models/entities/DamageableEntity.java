package pjvsemproj.models.entities;

public abstract class DamageableEntity extends Entity implements Damageable {

    protected int health;
    protected int maxHealth;

    protected DamageableEntity(boolean isPassable) {
        super(isPassable);
    }

    @Override
    public void takeDamage(int damage) {
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

    @Override
    public void takeHeal(int heal) {
        this.health += heal;

        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;

        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }
}
