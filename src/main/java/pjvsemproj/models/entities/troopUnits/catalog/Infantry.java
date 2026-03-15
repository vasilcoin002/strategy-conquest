package pjvsemproj.models.entities.troopUnits.catalog;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class Infantry extends TroopUnit {
    public Infantry(int x, int y) {
        super(x, y);
        this.maxStamina = 2;
        this.maxHealth = 50;
        this.attackRange = 1;
        this.maxDamage = 20;
        this.minDamage = 15;

        this.stamina = maxStamina;
        this.health = maxHealth;
    }
}
