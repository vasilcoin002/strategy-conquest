package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;

public class RelationshipManager {

    public void changeCityOwner(City city, Player newOwner) {
        Player oldOwner = city.getOwner();
        if (oldOwner != null) {
            oldOwner.removeCity(city);
        }

        city.setOwner(newOwner);
        if (newOwner != null) {
            newOwner.addCity(city);
        }
    }

    public void addTroopUnitToPlayer(TroopUnit troopUnit, Player player) {
        troopUnit.setOwner(player);
        player.addTroopUnit(troopUnit);
    }
}
