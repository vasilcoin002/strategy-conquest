package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.Upgradable;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;


/**
 * Represents a city placed on the map.
 *
 * A city may belong to a player, generate gold every turn,
 * and be upgraded to stronger economic levels.
 */
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

//    public City(Tile tile, CityType cityType, Player owner) {
//        this(tile, cityType);
//        this.owner = owner;
//    }

    /**
     * Checks whether the city can be upgraded.
     *
     * @return {@code true} if a next city level exists
     */
    @Override
    public boolean canBeUpgraded() {
        return this.cityType.nextCityType != null;
    }


    /**
     * Upgrades the city to the next available level.
     * Does nothing if the city is already at maximum level.
     */
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
