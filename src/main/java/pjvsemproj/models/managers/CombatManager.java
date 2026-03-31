package pjvsemproj.models.managers;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.managers.helpers.GridPositionHelper;
import pjvsemproj.models.managers.helpers.OwnershipHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombatManager implements ITurnListener{

    private Player currentPlayer;
    private GameMap gameMap;

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

    /**
     * Returns all enemy troop units that attacker can hit this turn.
     */
    public Set<TroopUnit> getAttackableTroops(TroopUnit attacker) {
        Set<TroopUnit> result = new HashSet<>();

        if (!attacker.hasAttackedThisTurn()) {
            return result;
        }

        Tile startTile = attacker.getTile();
        int range = attacker.getAttackRange();

        int sx = startTile.getX();
        int sy = startTile.getY();

        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {

                Tile tile = gameMap.getTile(x, y);
                if (tile == null || tile == startTile) {
                    continue;
                }
                int distance = Math.abs(sx - x) + Math.abs(sy - y);

                if (distance > range) {
                    continue;
                }
                for (IGridEntity entity : tile.getEntities()) {
                    if (entity instanceof TroopUnit target) {
                        if (target.getOwner() != attacker.getOwner()) {
                            result.add(target);
                        }
                    }
                }
            }
        }
        return result;
    }


    public Set<Tile> getAttackableTiles(TroopUnit attacker) {
        Set<Tile> tiles = new HashSet<>();
        for (TroopUnit troop : getAttackableTroops(attacker)) {
            tiles.add(troop.getTile());
        }

        return tiles;
    }

    /**
     * Performs attack using TroopUnit logic.
     */
    public boolean attackTroop(TroopUnit attacker, TroopUnit target) {
        if (attacker.hasAttackedThisTurn()){
            return false;
        }
        if (attacker == target){
            return false;
        }
        if (!getAttackableTroops(attacker).contains(target)) {
            return false;
        }
        int damage = attacker.calculateDamage();
        target.takeDamage(damage);

        if (target.isDead()) {
            OwnershipHelper.removeTroopUnitFromPlayer(target);
            GridPositionHelper.removeFromBoard(target);
        }
        attacker.setHasAttackedThisTurn(true);

        return true;
    }
}
