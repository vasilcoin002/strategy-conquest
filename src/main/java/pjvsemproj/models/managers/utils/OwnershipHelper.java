package pjvsemproj.models.managers.utils;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

/**
 * Helper class for syncing bidirectional relationship between Ownable and Player
 */
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
     * Safely transfers a TroopUnit to a new player. Also used for adding a new unit to player
     */
    public static boolean addTroopUnitToPlayer(TroopUnit troopUnit, Player newOwner) {
        if (troopUnit == null || newOwner == null) return false;

        Player oldOwner = troopUnit.getOwner();
        if (oldOwner == newOwner) return false;

        if (oldOwner != null) {
            oldOwner.removeTroopUnit(troopUnit);
        }

        troopUnit.setOwner(newOwner);
        newOwner.addTroopUnit(troopUnit);
        return true;
    }

    public static boolean removeTroopUnitFromPlayer(TroopUnit troopUnit, Player player) {
        if (troopUnit == null || player == null) return false;
        return player.removeTroopUnit(troopUnit);
    }

    public static boolean removeTroopUnitFromPlayer(TroopUnit troopUnit) {
        return removeTroopUnitFromPlayer(troopUnit, troopUnit.getOwner());
    }
}