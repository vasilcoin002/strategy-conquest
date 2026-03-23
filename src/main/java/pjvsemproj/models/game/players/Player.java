package pjvsemproj.models.game.players;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected final String name;
    protected final List<City> cities;
    protected final List<TroopUnit> troops;
    protected int balance;

    public Player(String name) {
        this.name = name;
        this.cities = new ArrayList<>();
        this.troops = new ArrayList<>();
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public List<City> getCities() {
        return cities;
    }

    public List<TroopUnit> getTroops() {
        return troops;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
