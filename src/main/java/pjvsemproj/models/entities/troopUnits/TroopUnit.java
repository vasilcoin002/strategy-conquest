package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;


/**
 * Represents a troop unit controlled by a player.
 *
 * A troop unit has health, damage range, movement range,
 * attack range and per-turn movement and attack state.
 */
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
        // hasMovedThisTurn = false => player can spawn many units
        // in one city per round
        // hasAttackedThisTurn = false => player can spawn and unexpectedly
        // attack enemies
        this(troopType, city.getTile(), true, true);
    }

    public TroopUnit(TroopType troopType, Tile tile,
                     boolean hasMovedThisTurn, boolean hasAttackedThisTurn) {
        super(tile, false);

        this.name = troopType.name();

        this.movementRange = troopType.movementRange;
        this.maxHealth = troopType.maxHealth;
        this.attackRange = troopType.attackRange;
        this.maxDamage = troopType.maxDamage;
        this.minDamage = troopType.minDamage;

        this.health = maxHealth;
        this.hasMovedThisTurn = hasMovedThisTurn;
        this.hasAttackedThisTurn = hasAttackedThisTurn;
    }

    /**
     * Calculates random damage within the unit's damage interval.
     *
     * @return generated damage value
     */

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

    @Override
    public int getMinDamage() {
        return minDamage;
    }

    @Override
    public int getMaxDamage() {
        return maxDamage;
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }
}
