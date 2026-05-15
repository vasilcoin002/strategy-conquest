package pjvsemproj.models.managers.utils;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

/**
 * Utility class for synchronizing the bidirectional relationship between Ownable entities and {@link Player}s.
 * <p>
 * Ensures that when an entity changes hands (e.g., via conquest or spawning), both the entity's owner reference
 * and the player's internal list of owned entities are updated atomically.
 */
public class OwnershipHelper {

    /**
     * Safely transfers ownership of a {@link City} to a new player.
     * Automatically removes the city from its previous owner's control if it was already owned.
     *
     * @param city     the city being conquered or transferred
     * @param newOwner the new player taking ownership
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
     * Safely assigns a {@link TroopUnit} to a new player.
     * Automatically removes the troop from its previous owner's control if it was already owned.
     *
     * @param troopUnit the troop unit being assigned
     * @param newOwner  the player taking command of the unit
     * @return {@code true} if the unit was successfully assigned, {@code false} if parameters are null or ownership is unchanged
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

    /**
     * Explicitly removes a specific {@link TroopUnit} from a specific player's control.
     *
     * @param troopUnit the troop unit to remove
     * @param player    the player losing control of the unit
     * @return {@code true} if the unit was successfully removed from the player's list
     */
    public static boolean removeTroopUnitFromPlayer(TroopUnit troopUnit, Player player) {
        if (troopUnit == null || player == null) return false;
        return player.removeTroopUnit(troopUnit);
    }

    /**
     * Removes a {@link TroopUnit} from its current owner's control without needing the player reference.
     *
     * @param troopUnit the troop unit to strip from its owner
     * @return {@code true} if the unit was successfully removed from an owner, {@code false} if it had no owner
     */
    public static boolean removeTroopUnitFromPlayer(TroopUnit troopUnit) {
        return removeTroopUnitFromPlayer(troopUnit, troopUnit.getOwner());
    }
}