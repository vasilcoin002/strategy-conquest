package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.DamageableEntity;
import pjvsemproj.models.entities.Upgradable;

public class City extends DamageableEntity implements Upgradable {

    protected CityType cityType;

    protected City() {
        super(true);

        this.cityType = CityType.LEVEL_1;
        this.maxHealth = 100;
        this.health = maxHealth;
    }

    // TODO implement method
    @Override
    public void upgrade() {
        if (this.cityType.nextCityType != null) {
            this.cityType = this.cityType.nextCityType;
        }
    }
}
