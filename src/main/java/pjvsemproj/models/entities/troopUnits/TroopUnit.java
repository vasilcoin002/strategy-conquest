package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.GameConstants;
import pjvsemproj.models.entities.Damageable;
import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.IDamager;
import pjvsemproj.models.entities.ILiving;

public abstract class TroopUnit extends Entity implements ILiving, IDamager {

    protected int health;
    protected int maxHealth;

    protected int minDamage;
    protected int maxDamage;
    protected int attackRange;

    protected int maxStamina;
    protected int stamina;

    public TroopUnit() {
        super(false);
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

    @Override
    public void attack(Damageable damageTaker) {
        int additionalDamageRange = maxDamage - minDamage;
        // casting to float Math.random() because Math.round() returns float if there is argument of type double
        damageTaker.takeDamage(
                minDamage +
                Math.round(additionalDamageRange * (float)Math.random())
        );
    }

    @Override
    public void moveUp(int step) {
        if (step <= this.stamina) {
            this.y += step * GameConstants.CELL_SIZE;
            this.stamina -= step;
        }
    }

    @Override
    public void moveDown(int step) {
        if (step <= this.stamina) {
            this.y -= step * GameConstants.CELL_SIZE;
            this.stamina -= step;
        }
    }

    @Override
    public void moveLeft(int step) {
        if (step <= this.stamina) {
            this.x -= step * GameConstants.CELL_SIZE;
            this.stamina -= step;
        }
    }

    @Override
    public void moveRight(int step) {
        if (step <= this.stamina) {
            this.x += step * GameConstants.CELL_SIZE;
            this.stamina -= step;
        }
    }

    @Override
    public int getStamina() {
        return this.stamina;
    }

    @Override
    public void refreshStamina() {
        this.stamina = maxStamina;
    }
}
