package pjvsemproj.models.managers;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.MovementNode;

import java.util.*;
import java.util.logging.Logger;


/**
 * Handles movement logic including calculating reachable tiles, validating movement, and executing movement.
 * <p>
 * Leverages path search mechanics to isolate navigation cells, verifies
 * multi-user tile blockages, and interfaces with the {@link ConquestManager} to resolve capture triggers.
 */
public class MovementManager implements ITurnListener {
    private static final Logger LOGGER = Logger.getLogger(MovementManager.class.getName());

    private Player currentPlayer;
    private final GameMap gameMap;
    private final ConquestManager conquestManager;

    /**
     * Constructs a movement manager instance linked to map layouts and conquest components.
     *
     * @param gameMap         The master grid map layout tracking positions.
     * @param currentPlayer   The active player instance starting the turn.
     * @param conquestManager Manager handling city capture triggers upon movement completion.
     */
    public MovementManager(GameMap gameMap, Player currentPlayer, ConquestManager conquestManager) {
        this.currentPlayer = currentPlayer;
        this.gameMap = gameMap;
        this.conquestManager = conquestManager;
    }

    /**
     * Refreshes local participant tracking structures and clears movement fatigue counters.
     *
     * @param activePlayer The {@link Player} assuming active turn controls.
     */
    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        activePlayer.getTroops().forEach(
                troop -> troop.setHasMovedThisTurn(false)
        );
    }

    /**
     * Post-turn handler invoked when active player option blocks wrap up.
     *
     * @param endingPlayer The {@link Player} whose turn option block is wrapping up.
     */
    @Override
    public void onTurnEnd(Player endingPlayer) {}

    /**
     * Finds all tiles that the given troop unit can reach in the current turn
     * using the Breadth-First Search (BFS) algorithm.
     * <p>
     * BFS explores the map level by level, starting from the unit's current position.
     * Each step represents moving to a neighboring tile, increasing the distance by 1.
     * <p>
     * The algorithm ensures:
     * - Only tiles within the unit's movement range are considered
     * - Each tile is visited at most once with the shortest distance
     * - Movement rules are respected (e.g., cannot move onto a tile occupied by another unit)
     * <p>
     * A queue is used to process tiles in order of increasing distance (FIFO),
     * which guarantees correct distance-based expansion.
     *
     * @param troopUnit the unit for which reachable tiles are calculated.
     * @return a set of tiles that the unit can move to this turn.
     */
    public Set<Tile> getAvailableTilesForMovement(TroopUnit troopUnit) {
        Set<Tile> reachableTiles = new HashSet<>();
        if (!canPlayerControlTroop(troopUnit) || troopUnit.hasMovedThisTurn()) return reachableTiles;

        Tile startTile = troopUnit.getTile();
        if (startTile == null) return reachableTiles;

        int movementRange = troopUnit.getMovementRange();

        Queue<MovementNode> queue = new LinkedList<>();
        Map<Tile, Integer> visitedDistances = new HashMap<>();

        queue.add(new MovementNode(startTile, 0));
        visitedDistances.put(startTile, 0);

        while (!queue.isEmpty()) {
            MovementNode currentNode = queue.poll();
            Tile currentTile = currentNode.tile();
            int currentDistance = currentNode.distance();

            if (currentDistance > movementRange) {
                continue;
            }

            reachableTiles.add(currentTile);

            for (Tile neighborTile : getNeighbors(currentTile)) {
                int nextDistance = currentDistance + 1;

                if (nextDistance > movementRange) {
                    continue;
                }

                if (!canMoveThroughOrToTile(troopUnit, neighborTile, startTile)) {
                    continue;
                }

                Integer knownDistance = visitedDistances.get(neighborTile);
                if (knownDistance == null || nextDistance < knownDistance) {
                    visitedDistances.put(neighborTile, nextDistance);
                    queue.add(new MovementNode(neighborTile, nextDistance));
                }
            }
        }

        reachableTiles.remove(startTile);
        return reachableTiles;
    }

    /**
     * Moves a troop unit to the target tile if movement is valid.
     * <p>
     * Validates null entries, checks control permissions, and cross-references reachable path sets.
     * If valid, updates position metadata, consumes turn actions, and evaluates settlement conquest triggers.
     *
     * @param troopUnit  The target {@link TroopUnit} entity undergoing relocation.
     * @param targetTile The target {@link Tile} cell location destination.
     * @return {@code true} if relocation succeeded and actions locked; {@code false} if parameters matched illegal paths.
     */
    public boolean moveTroopUnit(TroopUnit troopUnit, Tile targetTile) {
        if (troopUnit == null || targetTile == null) {
            return false;
        }

        if (!canPlayerControlTroop(troopUnit)) {
            return false;
        }

        if (troopUnit.hasMovedThisTurn()) {
            LOGGER.fine("Movement rejected: Unit " + troopUnit.getId() + " has already moved this turn.");
            return false;
        }

        Set<Tile> availableTiles = getAvailableTilesForMovement(troopUnit);
        if (!availableTiles.contains(targetTile)) {
            return false;
        }

        boolean moved = GridPositionHelper.moveEntity(troopUnit, targetTile);
        if (moved) {
            troopUnit.setHasMovedThisTurn(true);
        }

        for (IGridEntity entity : targetTile.getEntities()) {
            if (entity instanceof City city) {
                conquestManager.conquerCity(troopUnit, city);
            }
        }

        return moved;
    }

    /**
     * Checks whether the current player can control the given troop unit.
     * <p>
     * Enforces context checks matching unit ownership tags against turn keys.
     *
     * @param troopUnit The specific {@link TroopUnit} evaluated for active ownership permissions.
     * @return {@code true} if control properties match active user names; {@code false} if control is denied.
     */
    public boolean canPlayerControlTroop(TroopUnit troopUnit) {
        return troopUnit != null
                && currentPlayer != null
                && Objects.equals(
                troopUnit.getOwner().getName(),
                currentPlayer.getName()
        );
    }

    /**
     * Returns all valid neighboring tiles (4-directional).
     * <p>
     * Performs boundary index checks to collect orthogonal cell records.
     *
     * @param tile The reference {@link Tile} index root node.
     * @return A {@link List} containing adjacent verified {@link Tile} cell elements.
     */
    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int x = tile.getX();
        int y = tile.getY();

        addTileIfValid(neighbors, x + 1, y);
        addTileIfValid(neighbors, x - 1, y);
        addTileIfValid(neighbors, x, y + 1);
        addTileIfValid(neighbors, x, y - 1);

        return neighbors;
    }

    /**
     * Checks whether a unit can move through or enter a tile.
     * <p>
     * Filters out null targets and blockages caused by stationary rival units.
     *
     * @param troopUnit The moving {@link TroopUnit} requesting navigation clearances.
     * @param tile      The target destination {@link Tile} being evaluated.
     * @param startTile The original origin cell where path searches began.
     * @return {@code true} if navigation through or onto this block is valid; {@code false} if blocked.
     */
    private boolean canMoveThroughOrToTile(TroopUnit troopUnit, Tile tile, Tile startTile) {
        if (tile == null) {
            return false;
        }

        if (tile == startTile) {
            return true;
        }

        return !containsAnotherTroopUnit(tile, troopUnit);
    }

    /**
     * Adds a tile to the destination collector if its grid coordinates match map bounds.
     *
     * @param neighbors The target list holding valid cell neighbors.
     * @param x         The grid horizontal column index coordinate.
     * @param y         The grid vertical row index coordinate.
     */
    private void addTileIfValid(List<Tile> neighbors, int x, int y) {
        Tile tile = gameMap.getTile(x, y);
        if (tile != null) {
            neighbors.add(tile);
        }
    }

    /**
     * Checks if the tile contains another troop unit (blocking movement).
     *
     * @param tile        The target {@link Tile} node space under examination.
     * @param movingTroop The reference entity tracking current movement paths.
     * @return {@code true} if another unit blocks this grid cell; {@code false} if clear.
     */
    private boolean containsAnotherTroopUnit(Tile tile, TroopUnit movingTroop) {
        return tile.getEntities().stream()
                .anyMatch(entity -> entity instanceof TroopUnit && entity != movingTroop);
    }
}