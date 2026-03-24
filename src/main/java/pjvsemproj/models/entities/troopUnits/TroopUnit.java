package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.players.Player;

// TODO add and implement interface Purchasable
// TODO connect with Game class
// TODO change methods, that teammates cannot damage each other
public class TroopUnit extends DamageableEntity implements Movable, IDamager {

    private final String name;
    private Player owner;

    private final int minDamage;
    private final int maxDamage;
    private final int attackRange;

    private final int movementRange;

    public TroopUnit(TroopType troopType, City city) {
        super(city.getX(), city.getY(), false);

        this.name = troopType.name();

        this.movementRange = troopType.movementRange;
        this.maxHealth = troopType.maxHealth;
        this.attackRange = troopType.attackRange;
        this.maxDamage = troopType.maxDamage;
        this.minDamage = troopType.minDamage;

        this.health = maxHealth;
    }

    // TODO move to manager
    @Override
    public void attack(Damageable damageTaker) {
        int additionalDamageRange = maxDamage - minDamage;
        // casting to float Math.random() because Math.round() returns float if there is argument of type double
        damageTaker.takeDamage(
                minDamage +
                Math.round(additionalDamageRange * (float)Math.random())
        );
    }

    // TODO move moving methods to manager and rewrite them, so it can move to specific coordinates
//    @Override
    public void moveUp(int step) {
        if (step <= this.movementRange) {
            this.y += step;
        }
    }

//    @Override
    public void moveDown(int step) {
        if (step <= this.movementRange) {
            this.y -= step;
        }
    }

//    @Override
    public void moveLeft(int step) {
        if (step <= this.movementRange) {
            this.x -= step;
        }
    }

//    @Override
    public void moveRight(int step) {
        if (step <= this.movementRange) {
            this.x += step;
        }
    }

    public String getName() {
        return this.name;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public int getMovementRange() {
        return this.movementRange;
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
}
