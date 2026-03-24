package pjvsemproj.models.game.players;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;

import java.util.ArrayList;
import java.util.List;

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

    public void addCity(City city) {
        if (!cities.contains(city)) {
            cities.add(city);
            // TODO changeOwner move to manager if it is needed
            city.changeOwner(this);
        }
    }

    public void removeCity(City city) {
        cities.remove(city);
    }

    public List<TroopUnit> getTroops() {
        return troops;
    }

    public void addTroopUnit(TroopUnit troopUnit) {
        troops.add(troopUnit);
    }

    public void removeTroopUnit(TroopUnit troopUnit) {
        troops.remove(troopUnit);
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
