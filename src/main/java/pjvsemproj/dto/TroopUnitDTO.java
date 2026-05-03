package pjvsemproj.dto;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class TroopUnitDTO extends EntityDTO {
    public final int hp;
    // adding transient to tell Gson to not serialize it
    public final transient int maxHp;
    public final transient int minDamage;
    public final transient int maxDamage;
    public final boolean hasMovedThisTurn;
    public final boolean hasAttackedThisTurn;

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