package pjvsemproj.models.entities.troopUnits.catalog;

import pjvsemproj.models.entities.troopUnits.TroopUnit;

public class Cavalry extends TroopUnit {
    public Cavalry(int x, int y) {
        super(x, y);
        this.maxStamina = 5;
        this.maxHealth = 45;
        this.attackRange = 1;
        this.maxDamage = 20;
        this.minDamage = 15;

        this.stamina = maxStamina;
        this.health = maxHealth;
    }
}
