package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.*;

// TODO add and implement interface Purchasable
// TODO connect with Game class
// TODO change methods, that teammates cannot damage each other
public abstract class TroopUnit extends DamageableEntity implements Movable, IDamager {

    protected int minDamage;
    protected int maxDamage;
    protected int attackRange;

    protected int maxStamina;
    protected int stamina;

    public TroopUnit(int x, int y) {
        super(false);
        this.x = x;
        this.y = y;
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
            this.y += step;
            this.stamina -= step;
        }
    }

    @Override
    public void moveDown(int step) {
        if (step <= this.stamina) {
            this.y -= step;
            this.stamina -= step;
        }
    }

    @Override
    public void moveLeft(int step) {
        if (step <= this.stamina) {
            this.x -= step;
            this.stamina -= step;
        }
    }

    @Override
    public void moveRight(int step) {
        if (step <= this.stamina) {
            this.x += step;
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getMaxStamina() {
        return maxStamina;
    }
}
