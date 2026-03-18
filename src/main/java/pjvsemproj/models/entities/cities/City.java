package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.DamageableEntity;
import pjvsemproj.models.entities.Upgradable;

public class City extends DamageableEntity implements Upgradable {

    protected int level;
    protected int generatesGold;

    protected City() {
        super(true);

        this.level = 1;
        this.maxHealth = 100;
        this.health = maxHealth;
        this.generatesGold = 50;
    }

    // TODO implement method
    @Override
    public void upgrade() {

    }
}
