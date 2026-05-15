package pjvsemproj.models.entities.cities;

import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.Ownable;
import pjvsemproj.models.entities.Upgradable;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;

import java.util.UUID;

/**
 * Represents a city placed on the map.
 * <p>
 * A city may belong to a player, generate gold every turn,
 * and be upgraded to stronger economic levels.
 */
public class City extends Entity implements Upgradable<CityType>, Ownable {

    private Player owner;
    private CityType cityType;

    /**
     * Constructs a city without an assigned tile (often used for testing or pre-placement setup).
     *
     * @param cityType the initial level/type of the city
     */
    public City(CityType cityType) {
        this(null, cityType);
    }

    /**
     * Constructs a city at a specific tile with an auto-generated ID.
     * Cities are impassable by default.
     *
     * @param tile     the map tile where the city is located
     * @param cityType the initial level/type of the city
     */
    public City(Tile tile, CityType cityType) {
        this(UUID.randomUUID().toString(), tile, cityType);
    }

    /**
     * Constructs a city at a specific tile with a specific ID.
     *
     * @param id       the specific unique identifier for this city
     * @param tile     the map tile where the city is located
     * @param cityType the initial level/type of the city
     */
    public City(String id, Tile tile, CityType cityType) {
        super(id, tile, true);
        this.cityType = cityType;
    }

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

    /**
     * Retrieves the player who currently owns the city.
     *
     * @return the owning player, or {@code null} if unowned/neutral
     */
    @Override
    public Player getOwner() {
        return owner;
    }

    /**
     * Assigns ownership of the city to a specific player.
     *
     * @param owner the new owning player
     */
    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Returns the underlying CityType enum representing this city's stats.
     *
     * @return the city type
     */
    public CityType getCityType() {
        return cityType;
    }

    /**
     * Returns the current upgrade level of the city.
     *
     * @return the current city level
     */
    @Override
    public CityType getCurrentLevel() {
        return cityType;
    }

    /**
     * Returns the amount of gold this city generates per economy turn.
     *
     * @return gold production rate based on current city type
     */
    public int getGoldProducedPerRound() {
        return cityType.goldProducedPerRound;
    }

    /**
     * Returns the amount of gold this city requires to become upgraded.
     *
     * @return gold required for upgrading based on current city type
     */
    public int getUpgradePrice() {
        return cityType.upgradePrice;
    }
}