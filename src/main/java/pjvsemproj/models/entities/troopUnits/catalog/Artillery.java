package pjvsemproj.models.entities.troopUnits.catalog;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class Artillery extends TroopUnit {
    public Artillery(int x, int y) {
        super(x, y);
        this.maxStamina = 1;
        this.maxHealth = 40;
        this.attackRange = 3;
        this.maxDamage = 35;
        this.minDamage = 25;

        this.stamina = maxStamina;
        this.health = maxHealth;

    }
}
