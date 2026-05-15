package pjvsemproj.models.game.players;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract class for all players in the game.
 * <p>
 * A player owns cities and troop units and manages a gold balance.
 */
public abstract class Player {
    protected final String name;
    protected final List<City> cities;
    protected final List<TroopUnit> troops;
    protected int balance;

    /**
     * Constructs a new player with a starting gold balance.
     *
     * @param name           the display name of the player
     * @param initialBalance the starting amount of gold
     */
    public Player(String name, int initialBalance) {
        this.name = name;
        this.cities = new ArrayList<>();
        this.troops = new ArrayList<>();
        this.balance = initialBalance;
    }

    /**
     * Retrieves the player's name.
     *
     * @return the name string
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of all cities currently owned by this player.
     *
     * @return the list of owned cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     * Safely assigns a new city to this player's ownership.
     * Prevents adding null references or duplicate cities.
     *
     * @param city the city to add
     * @return {@code true} if the city was successfully added, {@code false} if it was null or already owned
     */
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

    /**
     * Retrieves the list of all troop units currently commanded by this player.
     *
     * @return the list of owned troops
     */
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

    /**
     * Retrieves the current amount of gold the player has.
     *
     * @return the current gold balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Directly sets the player's gold balance.
     *
     * @param balance the exact amount of gold to assign
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Increases the player's current gold balance by a specific amount.
     *
     * @param gold the amount of gold to add
     */
    public void addGold(int gold) {
        this.balance += gold;
    }

    /**
     * Attempts to deduct gold from the player's balance.
     * Will fail if the player cannot afford the specified amount.
     *
     * @param amount the cost to deduct
     * @return {@code true} if the transaction was successful, {@code false} if the player lacks sufficient funds
     */
    public boolean spendGold(int amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}