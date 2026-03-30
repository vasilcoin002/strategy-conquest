package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.Upgradable;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;

public class City extends Entity implements Upgradable<CityType>, Ownable {

    private Player owner;
    private CityType cityType;

    public City(Tile tile, CityType cityType) {
        super(tile, true);
        this.cityType = cityType;
    }

    public City(CityType cityType) {
        this(null, cityType);
    }

    @Override
    public boolean canBeUpgraded() {
        return this.cityType.nextCityType != null;
    }

    public void upgrade() {
        if (canBeUpgraded()) {
            this.cityType = this.cityType.nextCityType;
        }
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public CityType getCityType() {
        return cityType;
    }

    @Override
    public CityType getCurrentLevel() {
        return cityType;
    }

    public int getGoldProducedPerRound() {
        return cityType.goldProducedPerRound;
    }

    public int getUpgradePrice() {
        return cityType.upgradePrice;
    }
}
