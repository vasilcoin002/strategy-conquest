package pjvsemproj.models.game.players;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Base abstract class for all players in the game.
 *
 * A player owns cities and troop units and manages a gold balance.
 */
public abstract class Player {
    protected final String name;
    protected final List<City> cities;
    protected final List<TroopUnit> troops;
    protected int balance;

    public Player(String name, int initialBalance) {
        this.name = name;
        this.cities = new ArrayList<>();
        this.troops = new ArrayList<>();
        this.balance = initialBalance;
    }

    public String getName() {
        return name;
    }

    public List<City> getCities() {
        return cities;
    }

    public boolean addCity(City city) {
        if (!cities.contains(city) && city != null) {
            return cities.add(city);
        }
        return false;
    }

    /**
     * Removes a city from this player's ownership.
     *
     * @param city city to remove
     * @return {@code true} if the city was removed
     */

    public boolean removeCity(City city) {
        return cities.remove(city);
    }

    public List<TroopUnit> getTroops() {
        return troops;
    }

    /**
     * Adds a troop unit to the player.
     *
     * @param troopUnit unit to add
     * @return {@code true} if the unit was added successfully
     */

    public boolean addTroopUnit(TroopUnit troopUnit) {
        return troops.add(troopUnit);
    }


    /**
     * Removes a troop unit from the player.
     *
     * @param troopUnit unit to remove
     * @return {@code true} if the unit was removed
     */
    public boolean removeTroopUnit(TroopUnit troopUnit) {
        return troops.remove(troopUnit);
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void addGold(int gold) {
        this.balance += gold;
    }

    public boolean spendGold(int gold) {
        if (balance > gold) {
            balance -= gold;
            return true;
        }
        return false;
    }
}
