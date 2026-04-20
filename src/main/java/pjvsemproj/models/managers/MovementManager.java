package pjvsemproj.models.managers;

import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.managers.utils.MovementNode;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.managers.utils.GridPositionHelper;

import java.util.*;

public class MovementManager implements ITurnListener {

    private Player currentPlayer;
    private final GameMap gameMap;

    public MovementManager(GameMap gameMap) {
        this(null, gameMap);
    }

    public MovementManager(Player currentPlayer, GameMap gameMap) {
        this.currentPlayer = currentPlayer;
        this.gameMap = gameMap;
    }

    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        activePlayer.getTroops().forEach(
                troop -> troop.setHasMovedThisTurn(false)
        );
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {}


    /**
     * Finds all tiles that the given troop unit can reach in the current turn
     * using the Breadth-First Search (BFS) algorithm.
     *
     * BFS explores the map level by level, starting from the unit's current position.
     * Each step represents moving to a neighboring tile, increasing the distance by 1.
     *
     * The algorithm ensures:
     * - Only tiles within the unit's movement range are considered
     * - Each tile is visited at most once with the shortest distance
     * - Movement rules are respected (e.g., cannot move onto a tile occupied by another unit)
     *
     * A queue is used to process tiles in order of increasing distance (FIFO),
     * which guarantees correct distance-based expansion.
     *
     * @param troopUnit the unit for which reachable tiles are calculated
     * @return a set of tiles that the unit can move to this turn
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
     */
    public boolean moveTroopUnit(TroopUnit troopUnit, Tile targetTile) {
        if (troopUnit == null || targetTile == null) {
            return false;
        }

        if (!canPlayerControlTroop(troopUnit)) {
            return false;
        }

        if (troopUnit.hasMovedThisTurn()) {
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

        return moved;
    }


    /**
     * Checks whether the current player can control the given troop unit.
     */
    public boolean canPlayerControlTroop(TroopUnit troopUnit) {
        return troopUnit != null
                && currentPlayer != null
                && troopUnit.getOwner() == currentPlayer;
    }


    /**
     * Returns all valid neighboring tiles (4-directional).
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
     * Adds tile to the list if coordinates are valid.
     */
    private void addTileIfValid(List<Tile> neighbors, int x, int y) {
        Tile tile = gameMap.getTile(x, y);
        if (tile != null) {
            neighbors.add(tile);
        }
    }


    /**
     * Checks if the tile contains another troop unit (blocking movement).
     */
    private boolean containsAnotherTroopUnit(Tile tile, TroopUnit movingTroop) {
        return tile.getEntities().stream()
                .anyMatch(entity -> entity instanceof TroopUnit && entity != movingTroop);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}
