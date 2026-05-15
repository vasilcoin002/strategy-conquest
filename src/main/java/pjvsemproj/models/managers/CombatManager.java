package pjvsemproj.models.managers;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pjvsemproj.models.game.GameConstants.TROOP_HEALING_PERCENT_BY_MAX_HP;

// TODO optimize getAttackableTroops
//  change the loop bounds to only check the bounding box around the attacker (now it checks entire map)
/**
 * Handles combat logic including:
 * - attacking
 * - damage calculation
 * - unit death
 * - healing units in cities
 */
public class CombatManager implements ITurnListener{

    private Player currentPlayer;
    private final GameMap gameMap;

    /**
     * Constructs a CombatManager for a specific map and starting player.
     *
     * @param gameMap       the active game map
     * @param currentPlayer the player whose turn is initially active
     */
    public CombatManager(GameMap gameMap, Player currentPlayer) {
        this.gameMap = gameMap;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Refreshes the attack state for all troops belonging to the active player
     * and applies healing to units stationed inside friendly cities.
     *
     * @param activePlayer the player whose turn has just started
     */
    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        activePlayer.getTroops().forEach(
                troop -> troop.setHasAttackedThisTurn(false)
        );

        getTroopsToHeal().forEach(troopUnit -> {
            int heal = (int) (troopUnit.getMaxHealth() * TROOP_HEALING_PERCENT_BY_MAX_HP);
            troopUnit.takeHeal(heal);
        });
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    /**
     * Identifies all troops belonging to the current player that are eligible for healing.
     * Troops must be located on a tile containing a city owned by the same player.
     *
     * @return a list of friendly troops stationed in friendly cities
     */
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
     * Scans the map to find targets within the attacker's attack range using Manhattan distance.
     *
     * @param attacker the troop looking for targets
     * @return a set of attackable enemy units
     */
    public Set<TroopUnit> getAttackableTroops(TroopUnit attacker) {
        Set<TroopUnit> result = new HashSet<>();

        if (attacker.hasAttackedThisTurn() || attacker.getOwner() != currentPlayer) {
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

    /**
     * Retrieves the specific map tiles containing attackable enemies.
     *
     * @param attacker the troop looking for targets
     * @return a set of tiles within range containing enemy units
     */
    public Set<Tile> getAttackableTiles(TroopUnit attacker) {
        Set<Tile> tiles = new HashSet<>();
        for (TroopUnit troop : getAttackableTroops(attacker)) {
            tiles.add(troop.getTile());
        }

        return tiles;
    }

    /**
     * Performs attack using TroopUnit logic.
     * Applies damage to the target, removes it if its health reaches 0,
     * and exhausts the attacker's combat and movement actions for the turn.
     *
     * @param attacker the unit dealing damage
     * @param target   the unit receiving damage
     * @return {@code true} if the attack was successfully executed
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
        // added to forbid moving after attack
        attacker.setHasMovedThisTurn(true);

        return true;
    }
}