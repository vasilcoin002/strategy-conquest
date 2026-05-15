package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;

/**
 * Represents a troop unit controlled by a player.
 * <p>
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

    /**
     * Constructs a TroopUnit spawned at a specific City.
     * * @param troopType the classification/type of troop being spawned
     * @param city      the city where the troop is placed
     */
    public TroopUnit(TroopType troopType, City city) {
        // hasMovedThisTurn = false => player can spawn many unit in one city per round
        // hasAttackedThisTurn = false => player can spawn and unexpectedly attack enemies
        this(troopType, city.getTile(), true, true);
    }

    /**
     * Constructs a TroopUnit with an auto-generated ID at a specific tile.
     *
     * @param troopType           the classification/type of troop
     * @param tile                the starting map tile
     * @param hasMovedThisTurn    whether the troop has already exhausted its movement this turn
     * @param hasAttackedThisTurn whether the troop has already attacked this turn
     */
    public TroopUnit(TroopType troopType, Tile tile,
                     boolean hasMovedThisTurn, boolean hasAttackedThisTurn) {
        this(java.util.UUID.randomUUID().toString(), troopType, tile, hasMovedThisTurn, hasAttackedThisTurn);
    }

    /**
     * Constructs a TroopUnit with a specific ID at a specific tile.
     *
     * @param id                  the specific unique identifier for this troop
     * @param troopType           the classification/type of troop
     * @param tile                the starting map tile
     * @param hasMovedThisTurn    whether the troop has already exhausted its movement this turn
     * @param hasAttackedThisTurn whether the troop has already attacked this turn
     */
    public TroopUnit(String id, TroopType troopType, Tile tile,
                     boolean hasMovedThisTurn, boolean hasAttackedThisTurn) {
        super(id, tile, false); // Assuming troops block other troops
        this.name = troopType.name();
        this.minDamage = troopType.minDamage;
        this.maxDamage = troopType.maxDamage;
        this.attackRange = troopType.attackRange;
        this.movementRange = troopType.movementRange;
        this.maxHealth = troopType.maxHealth;
        this.health = this.maxHealth;
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
        return minDamage + Math.round(additionalDamageRange * (float) Math.random());
    }

    /**
     * Retrieves the display name of this troop unit.
     *
     * @return the name string
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the player who commands this troop.
     *
     * @return the owning player
     */
    @Override
    public Player getOwner() {
        return owner;
    }

    /**
     * Assigns command of this troop to a player.
     *
     * @param owner the new owning player
     */
    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Retrieves the maximum distance this unit can travel in a single turn.
     *
     * @return the movement range in tiles
     */
    @Override
    public int getMovementRange() {
        return this.movementRange;
    }

    /**
     * Checks if the unit has already used its movement action this turn.
     *
     * @return {@code true} if movement is exhausted
     */
    @Override
    public boolean hasMovedThisTurn() {
        return hasMovedThisTurn;
    }

    /**
     * Updates the movement exhaustion status of this unit.
     *
     * @param hasMovedThisTurn {@code true} to exhaust movement, {@code false} to refresh it
     */
    @Override
    public void setHasMovedThisTurn(boolean hasMovedThisTurn) {
        this.hasMovedThisTurn = hasMovedThisTurn;
    }

    /**
     * Checks if the unit has already attacked this turn.
     *
     * @return {@code true} if the attack action is exhausted
     */
    @Override
    public boolean hasAttackedThisTurn() {
        return hasAttackedThisTurn;
    }

    /**
     * Updates the attack exhaustion status of this unit.
     *
     * @param hasAttackedThisTurn {@code true} to exhaust attack, {@code false} to refresh it
     */
    @Override
    public void setHasAttackedThisTurn(boolean hasAttackedThisTurn) {
        this.hasAttackedThisTurn = hasAttackedThisTurn;
    }

    /**
     * Retrieves the minimum base damage this unit can deal.
     *
     * @return minimum damage value
     */
    @Override
    public int getMinDamage() {
        return minDamage;
    }

    /**
     * Retrieves the maximum base damage this unit can deal.
     *
     * @return maximum damage value
     */
    @Override
    public int getMaxDamage() {
        return maxDamage;
    }

    /**
     * Retrieves the range at which this unit can strike an enemy.
     *
     * @return attack range in tiles
     */
    @Override
    public int getAttackRange() {
        return attackRange;
    }
}