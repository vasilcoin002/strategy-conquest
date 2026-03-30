package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.players.Player;

public class TroopUnit extends DamageableEntity implements Movable, IDamager, Ownable {

    private final String name;
    private Player owner;

    private final int minDamage;
    private final int maxDamage;
    private final int attackRange;

    private final int movementRange;
    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;

    public TroopUnit(TroopType troopType, City city) {
        super(city.getTile(), false);

        this.name = troopType.name();

        this.movementRange = troopType.movementRange;
        this.maxHealth = troopType.maxHealth;
        this.attackRange = troopType.attackRange;
        this.maxDamage = troopType.maxDamage;
        this.minDamage = troopType.minDamage;

        this.health = maxHealth;
        // false => player can spawn many units in one city per round
        this.hasMovedThisTurn = true;
        this.hasAttackedThisTurn = true;
    }

    @Override
    public int calculateDamage() {
        int additionalDamageRange = maxDamage - minDamage;
        // casting to float Math.random() because Math.round() returns float if there is argument of type double
        return minDamage + Math.round(additionalDamageRange * (float)Math.random());
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public int getMovementRange() {
        return this.movementRange;
    }

    @Override
    public boolean hasMovedThisTurn() {
        return hasMovedThisTurn;
    }

    @Override
    public void setHasMovedThisTurn(boolean hasMovedThisTurn) {
        this.hasMovedThisTurn = hasMovedThisTurn;
    }

    @Override
    public boolean hasAttackedThisTurn() {
        return hasAttackedThisTurn;
    }

    @Override
    public void setHasAttackedThisTurn(boolean hasAttackedThisTurn) {
        this.hasAttackedThisTurn = hasAttackedThisTurn;
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
