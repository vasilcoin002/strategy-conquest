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

/**
 * Handles combat logic including:
 * - attacking
 * - damage calculation
 * - unit death
 * - healing units in cities.
 * <p>
 * This manager implements the {@link ITurnListener} interface to hook into turn transitions,
 * resetting offensive action counters and processing defensive health generation for stationary
 * garisoned units.
 */
public class CombatManager implements ITurnListener {

    private Player currentPlayer;
    private final GameMap gameMap;

    /**
     * Constructs a combat manager tracking instance bound to a map grid context.
     *
     * @param gameMap       The master strategic grid layout mapping out cell indexes.
     * @param currentPlayer The initially active player profile configuration whose turn is opening.
     */
    public CombatManager(GameMap gameMap, Player currentPlayer) {
        this.gameMap = gameMap;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Resets action flags for the active player's unit division and triggers city healing loops.
     * <p>
     * Iterates through the current participant's mobile units to allow attacks for the new turn,
     * calculates health recovery thresholds, and applies healing properties safely.
     *
     * @param activePlayer The {@link Player} profile instance currently assuming turn control.
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

    /**
     * Handles event hooks fired when a participant wraps up their turn execution sequence.
     *
     * @param endingPlayer The {@link Player} instance whose action window is closing.
     */
    @Override
    public void onTurnEnd(Player endingPlayer) {

    }

    /**
     * Scans all cities owned by the active user to aggregate an itemized checklist of garisoned units eligible for recovery.
     * <p>
     * A unit is deemed eligible for healing if it occupies a tile containing a settlement owned by the same player.
     *
     * @return A {@link List} containing all verified {@link TroopUnit} elements eligible for health points replenishment.
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
     * Calculates the exhaustive set of valid target enemy units matching the structural attack capabilities of a unit.
     * <p>
     * Evaluates action counters, ownership permissions, and measures Manhattan distance properties
     * across the map grid to filter out non-enemy or out-of-range targets.
     *
     * @param attacker The specific {@link TroopUnit} dispatching the capability request.
     * @return A {@link Set} of hostile {@link TroopUnit} occupants residing within targetable cell ranges.
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
     * Aggregates the geometric tile cell representations occupied by targetable hostile forces.
     *
     * @param attacker The specific {@link TroopUnit} executing range analysis parameters.
     * @return A {@link Set} of {@link Tile} spatial structures containing hit-eligible enemy targets.
     */
    public Set<Tile> getAttackableTiles(TroopUnit attacker) {
        Set<Tile> tiles = new HashSet<>();
        for (TroopUnit troop : getAttackableTroops(attacker)) {
            tiles.add(troop.getTile());
        }

        return tiles;
    }

    /**
     * Executes local combat actions, processes random damage range rolls, and applies health deductions.
     *
     * @param attacker The instigating {@link TroopUnit} resource dealing the blow.
     * @param target   The receiving {@link TroopUnit} resource taking the damage parameters.
     * @return {@code true} if the attack met all criteria and executed; {@code false} if the action is illegal.
     */
    public boolean attackTroop(TroopUnit attacker, TroopUnit target) {
        if (!canAttack(attacker, target)) {
            return false;
        }

        target.takeDamage(attacker.calculateDamage());
        finalizeAttack(attacker, target);

        return true;
    }

    /**
     * Authoritatively updates health points and synchronizes combat updates arriving from remote game hosts.
     *
     * @param attacker The instigating network {@link TroopUnit} reference asset.
     * @param target   The receiving network {@link TroopUnit} reference asset.
     * @param newHp    The explicit health point total declared by the server referee layer.
     * @return {@code true} if the synchronization action was applied successfully; {@code false} otherwise.
     */
    public boolean attackTroop(TroopUnit attacker, TroopUnit target, int newHp) {
        if (!canAttack(attacker, target)) {
            return false;
        }

        target.setHealth(newHp);
        finalizeAttack(attacker, target);

        return true;
    }

    /**
     * Evaluates standard game rule constraints to see if an engagement is valid.
     *
     * @param attacker The unit seeking to initiate an engagement.
     * @param target   The unit being targeted for engagement.
     * @return {@code true} if the configuration parameters allow the attack; {@code false} if forbidden.
     */
    private boolean canAttack(TroopUnit attacker, TroopUnit target) {
        if (attacker.hasAttackedThisTurn()) {
            return false;
        }
        if (attacker == target) {
            return false;
        }
        return getAttackableTroops(attacker).contains(target);
    }

    /**
     * Finalizes structural updates following an engagement, locks turn actions, and purges dead assets.
     * <p>
     * Evaluates the defender's life metrics; if dead, it removes it from player tracking lists
     * and clears it from board layouts. Locks movement and attack choices on the attacker.
     *
     * @param attacker The instigating unit whose turn action states must lock.
     * @param target   The defending unit evaluated for fatal condition patterns.
     */
    private void finalizeAttack(TroopUnit attacker, TroopUnit target) {
        if (target.isDead()) {
            OwnershipHelper.removeTroopUnitFromPlayer(target);
            GridPositionHelper.removeFromBoard(target);
        }

        attacker.setHasAttackedThisTurn(true);
        attacker.setHasMovedThisTurn(true); // Forbids moving after attack
    }
}