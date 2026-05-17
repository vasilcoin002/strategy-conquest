package pjvsemproj.dto;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

/**
 * Data Transfer Object representing military mobile units.
 * <p>
 * Extends {@link EntityDTO} to capture unit attributes required for combat resolution, path movement loops,
 * and turn-state action verification flags across network interfaces.
 */
public class TroopUnitDTO extends EntityDTO {
    public Integer hp;
    // adding transient to tell Gson to not serialize it
    public transient int maxHp;
    public transient int minDamage;
    public transient int maxDamage;
    public Boolean hasMovedThisTurn;
    public Boolean hasAttackedThisTurn;

    /**
     * Constructs a troop data transfer container with comprehensive statistical properties.
     *
     * @param id                  The unique metadata string tracking token assigned to this specific military unit instance.
     * @param entityType          Descriptive string descriptor identifying the specific unit type or sub-class configuration.
     * @param x                   The spatial grid coordinate position along the horizontal column map axis.
     * @param y                   The spatial grid coordinate position along the vertical row map axis.
     * @param ownerName           The distinct profile username string matching the player who commands this troop.
     * @param hp                  The current hit-points health tracking value remaining for this specific unit instance.
     * @param maxHp               The absolute maximum capacity capability baseline health limit designated for this unit configuration.
     * @param minDamage           The minimum possible variance combat output damage threshold calculated during attack cycles.
     * @param maxDamage           The maximum possible variance combat output damage threshold calculated during attack cycles.
     * @param hasMovedThisTurn    Flag tracking whether this unit has spent its action options for movement instructions this turn.
     * @param hasAttackedThisTurn Flag tracking whether this unit has spent its action options for offensive combat engagement this turn.
     */
    public TroopUnitDTO(
            String id, String entityType, int x, int y, String ownerName,
            int hp, int maxHp, int minDamage, int maxDamage,
            boolean hasMovedThisTurn, boolean hasAttackedThisTurn
    ) {
        super(id, entityType, x, y, ownerName);
        this.hp = hp;
        this.maxHp = maxHp;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.hasMovedThisTurn = hasMovedThisTurn;
        this.hasAttackedThisTurn = hasAttackedThisTurn;
    }

    /**
     * Contextual hydration constructor mapping active simulation units directly into isolated transfer objects.
     * <p>
     * Extracts coordinates, captures current health and combat statistics, and saves turn action states.
     *
     * @param troop The live active {@link TroopUnit} domain object instance to extract snapshot attributes from.
     */
    public TroopUnitDTO(TroopUnit troop) {
        super(
                troop.getId(),
                troop.getName(),
                troop.getTile().getX(),
                troop.getTile().getY(),
                troop.getOwner().getName()
        );

        this.hp = troop.getHealth();
        this.maxHp = troop.getMaxHealth();
        this.minDamage = troop.getMinDamage();
        this.maxDamage = troop.getMaxDamage();
        this.hasMovedThisTurn = troop.hasMovedThisTurn();
        this.hasAttackedThisTurn = troop.hasAttackedThisTurn();
    }
}