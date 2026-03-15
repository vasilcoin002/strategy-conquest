package pjvsemproj.models.entities.troopUnits.catalog;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class Militia extends TroopUnit {
    public Militia(int x, int y) {
        super(x, y);
        this.maxStamina = 3;
        this.maxHealth = 20;
        this.attackRange = 1;
        this.maxDamage = 10;
        this.minDamage = 5;

        this.stamina = maxStamina;
        this.health = maxHealth;
    }
}
