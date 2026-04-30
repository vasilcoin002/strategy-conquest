package pjvsemproj.dto;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class TroopUnitDTO extends EntityDTO {
    public final int hp;
    public final int maxHp;
    public final int minDamage;
    public final int maxDamage;
    public final boolean hasMovedThisTurn;

    public TroopUnitDTO(
            String id, String entityType, int x, int y, String ownerName,
            int hp, int maxHp, int minDamage, int maxDamage,
            boolean hasMovedThisTurn
    ) {
        super(id, entityType, x, y, ownerName);
        this.hp = hp;
        this.maxHp = maxHp;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.hasMovedThisTurn = hasMovedThisTurn;
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
    }
}