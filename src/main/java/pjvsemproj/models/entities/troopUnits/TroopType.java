package pjvsemproj.models.entities.troopUnits;

import pjvsemproj.models.entities.Purchasable;


/**
 * Defines available troop types and their combat, movement and cost attributes.
 */
public enum TroopType implements Purchasable {
    Militia(3, 20, 1, 5, 10, 10),
    Infantry(2, 50, 1, 15, 20, 20),
    Cavalry(5, 45, 1, 15, 20, 30),
    Artillery(1, 40, 3, 25, 35, 40);

    public final int movementRange, maxHealth, attackRange, maxDamage, minDamage, price;

    TroopType(int movementRange, int maxHealth, int attackRange, int minDamage, int maxDamage, int price) {
        this.movementRange = movementRange;
        this.maxHealth = maxHealth;
        this.attackRange = attackRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.price = price;
    }
    public int getPrice() {
        return this.price;
    }
}
