package pjvsemproj.models.managers.helpers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

public class OwnershipHelper {

    /**
     * Safely transfers a City to a new player.
     */
    public static void transferCity(City city, Player newOwner) {
        if (city == null || newOwner == null) return;

        Player oldOwner = city.getOwner();
        if (oldOwner == newOwner) return;

        if (oldOwner != null) {
            oldOwner.removeCity(city);
        }

        city.setOwner(newOwner);
        newOwner.addCity(city);
    }

    /**
     * Safely transfers a TroopUnit to a new player.
     */
    public static void transferTroopUnit(TroopUnit troopUnit, Player newOwner) {
        if (troopUnit == null || newOwner == null) return;

        Player oldOwner = troopUnit.getOwner();
        if (oldOwner == newOwner) return;

        if (oldOwner != null) {
            oldOwner.removeTroopUnit(troopUnit);
        }

        troopUnit.setOwner(newOwner);
        newOwner.addTroopUnit(troopUnit);
    }
}