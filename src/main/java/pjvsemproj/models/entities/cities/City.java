package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.Upgradable;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.maps.Tile;

public class City extends Entity implements Upgradable {

    private Player owner;
    private CityType cityType;

    public City(Tile tile, CityType cityType) {
        super(tile, true);
        this.cityType = cityType;
    }

    @Override
    public boolean canBeUpgraded() {
        return this.cityType.nextCityType != null;
    }

    // TODO Move method to manager
    public void upgrade() {
        if (canBeUpgraded()) {
            this.cityType = this.cityType.nextCityType;
        }
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    // TODO move method to manager
    public void changeOwner(Player newOwner) {
        if (this.owner == newOwner) {
            return;
        }
        if (this.owner != null) {
            this.owner.removeCity(this);
        }
        this.owner = newOwner;
        if (this.owner != null) {
            this.owner.addCity(this);
        }
    }

    public CityType getCityType() {
        return cityType;
    }
}
