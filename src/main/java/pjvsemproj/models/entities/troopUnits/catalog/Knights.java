package pjvsemproj.models.entities.troopUnits.catalog;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class Knights extends TroopUnit {
    public Knights(int x, int y) {
        super(x, y);
        this.maxStamina = 4;
        this.maxHealth = 100;
        this.attackRange = 1;
        this.maxDamage = 40;
        this.minDamage = 30;

        this.stamina = maxStamina;
        this.health = maxHealth;
    }
}
