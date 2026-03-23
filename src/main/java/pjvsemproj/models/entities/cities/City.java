package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.Upgradable;

public class City extends Entity implements Upgradable {

    protected CityType cityType;

    protected City() {
        super(true);

        this.cityType = CityType.LEVEL_1;
    }

    @Override
    public void upgrade() {
        if (this.cityType.nextCityType != null) {
            this.cityType = this.cityType.nextCityType;
        }
    }
}
