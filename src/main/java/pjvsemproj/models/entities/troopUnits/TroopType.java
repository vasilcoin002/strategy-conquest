package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.Purchasable;

/**
 * Defines available troop types and their immutable combat, movement, and cost attributes.
 * <p>
 * This enum acts as a configuration dictionary for the base stats of all units
 * spawned in the game.
 */
public enum TroopType implements Purchasable {

    /** Cheap, fast-moving unit with weak damage and low health. */
    Militia(3, 20, 1, 5, 10, 10),

    /** Standard frontline unit with high health and moderate damage. */
    Infantry(2, 50, 1, 15, 20, 20),

    /** Highly mobile unit with strong damage but slightly lower health than infantry. */
    Cavalry(5, 45, 1, 15, 20, 30),

    /** Slow, long-range siege unit capable of high damage from a distance. */
    Artillery(1, 40, 3, 25, 35, 40);

    /** The maximum number of tiles this unit can traverse in a single turn. */
    public final int movementRange;
    /** The starting and maximum health points for this unit. */
    public final int maxHealth;
    /** The maximum distance (in tiles) at which this unit can strike an enemy. */
    public final int attackRange;
    /** The absolute maximum damage this unit can randomly deal in a single strike. */
    public final int maxDamage;
    /** The guaranteed minimum damage this unit will deal in a single strike. */
    public final int minDamage;
    /** The economic cost to spawn this unit from a city. */
    public final int price;

    /**
     * Constructs a TroopType configuration.
     *
     * @param movementRange the movement limit per turn
     * @param maxHealth     the base health pool
     * @param attackRange   the striking distance
     * @param minDamage     the minimum attack damage
     * @param maxDamage     the maximum attack damage
     * @param price         the cost in gold to purchase
     */
    TroopType(int movementRange, int maxHealth, int attackRange, int minDamage, int maxDamage, int price) {
        this.movementRange = movementRange;
        this.maxHealth = maxHealth;
        this.attackRange = attackRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.price = price;
    }

    /**
     * Retrieves the cost required to purchase this troop type.
     *
     * @return the price in gold
     */
    @Override
    public int getPrice() {
        return this.price;
    }
}