package pjvsemproj.models.managers;

import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.maps.Tile;

import java.util.ArrayList;
import java.util.List;

public class CombatManager implements ITurnListener{

    private Player currentPlayer;

    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;

        getTroopsToHeal().forEach(troopUnit -> {
            int heal = (int) (troopUnit.getMaxHealth() * 0.25);
            troopUnit.takeHeal(heal);
        });
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    // TroopUnit gets heal if it stands in city on every player's turn
    public List<TroopUnit> getTroopsToHeal() {
        List<TroopUnit> troopsToHeal = new ArrayList<>();

        currentPlayer.getCities().forEach(city -> {
            Tile cityTile = city.getTile();
            cityTile.getEntities().forEach(entity -> {
                if (entity instanceof TroopUnit troopUnit) {
                    if (troopUnit.getOwner() == currentPlayer) {
                        troopsToHeal.add(troopUnit);
                    }
                }
            });
        });

        return troopsToHeal;
    }
}
