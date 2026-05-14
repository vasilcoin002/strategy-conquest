package pjvsemproj.models.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovementManagerTest {

    private GameMap gameMap;
    private Player player1;
    private Player player2;
    private MovementManager movementManager;

    @BeforeEach
    void setUp() {
        gameMap = new GameMap(10, 10);
        player1 = new HumanPlayer("Player 1", 100);
        player2 = new HumanPlayer("Player 2", 100);

        ConquestManager conquestManager = new ConquestManager(Arrays.asList(player1, player2), player1);
        movementManager = new MovementManager(gameMap, player1, conquestManager);
    }

    @Test
    void onTurnStart_ResetsMovementFlags_ForCurrentPlayerTroops() {
        TroopUnit troop = new TroopUnit(TroopType.Infantry, gameMap.getTile(0, 0), true, false);
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);

        assertTrue(troop.hasMovedThisTurn(), "Troop should initially have moved flag set");

        movementManager.onTurnStart(player1);

        assertFalse(troop.hasMovedThisTurn(), "Movement flag should be reset at the start of the turn");
    }

    @Test
    void getAvailableTilesForMovement_ValidUnit_ReturnsCorrectDiamondShape() {
        Tile startTile = gameMap.getTile(5, 5);
        TroopUnit troop = new TroopUnit(TroopType.Infantry, startTile, false, false); // Infantry has Range 2
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);
        GridPositionHelper.placeEntity(troop, startTile);

        Set<Tile> moves = movementManager.getAvailableTilesForMovement(troop);

        assertFalse(moves.isEmpty(), "Should find reachable tiles");
        assertFalse(moves.contains(startTile), "Start tile should not be in the reachable set");

        // Range 1 tests
        assertTrue(moves.contains(gameMap.getTile(5, 6)), "Should reach adjacent tile (0, 1)");
        assertTrue(moves.contains(gameMap.getTile(6, 5)), "Should reach adjacent tile (1, 0)");

        // Range 2 tests
        assertTrue(moves.contains(gameMap.getTile(5, 7)), "Should reach tile at exactly range 2 (0, 2)");
        assertTrue(moves.contains(gameMap.getTile(6, 6)), "Should reach diagonal tile at range 2 (1, 1)");

        // Range 3 tests (Out of bounds)
        assertFalse(moves.contains(gameMap.getTile(5, 8)), "Should NOT reach tile at range 3");
    }

    @Test
    void getAvailableTilesForMovement_BlockedByOtherTroop_RestrictsPath() {
        Tile startTile = gameMap.getTile(0, 0);
        TroopUnit movingTroop = new TroopUnit(TroopType.Infantry, startTile, false, false); // Range 2
        OwnershipHelper.addTroopUnitToPlayer(movingTroop, player1);
        GridPositionHelper.placeEntity(movingTroop, startTile);

        // Place a blocking troop directly in the path at (0,1)
        Tile blockingTile = gameMap.getTile(0, 1);
        TroopUnit blockingTroop = new TroopUnit(TroopType.Militia, blockingTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(blockingTroop, player2);
        GridPositionHelper.placeEntity(blockingTroop, blockingTile);

        Set<Tile> moves = movementManager.getAvailableTilesForMovement(movingTroop);

        assertFalse(moves.contains(blockingTile), "Cannot move onto a tile occupied by another troop");
        assertFalse(moves.contains(gameMap.getTile(0, 2)), "Cannot pathfind THROUGH a blocked tile");
        assertTrue(moves.contains(gameMap.getTile(1, 0)), "Can still move in unblocked directions");
    }

    @Test
    void getAvailableTilesForMovement_AlreadyMoved_ReturnsEmptySet() {
        Tile startTile = gameMap.getTile(5, 5);
        TroopUnit troop = new TroopUnit(TroopType.Infantry, startTile, true, false); // hasMoved = true
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);
        GridPositionHelper.placeEntity(troop, startTile);

        Set<Tile> moves = movementManager.getAvailableTilesForMovement(troop);

        assertTrue(moves.isEmpty(), "A unit that has already moved should have no available moves");
    }

    @Test
    void getAvailableTilesForMovement_EnemyUnit_ReturnsEmptySet() {
        Tile startTile = gameMap.getTile(5, 5);
        TroopUnit enemyTroop = new TroopUnit(TroopType.Infantry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(enemyTroop, player2); // Belongs to player 2
        GridPositionHelper.placeEntity(enemyTroop, startTile);

        Set<Tile> moves = movementManager.getAvailableTilesForMovement(enemyTroop);

        assertTrue(moves.isEmpty(), "Player 1 should not be able to see moves for Player 2's units");
    }

    @Test
    void moveTroopUnit_ValidMove_MovesUnitAndSetsFlag() {
        Tile startTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit troop = new TroopUnit(TroopType.Cavalry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);
        GridPositionHelper.placeEntity(troop, startTile);

        boolean success = movementManager.moveTroopUnit(troop, targetTile);

        assertTrue(success, "Valid movement should succeed");
        assertTrue(troop.hasMovedThisTurn(), "Unit's hasMovedThisTurn flag should be set to true");
        assertEquals(targetTile, troop.getTile(), "Unit's internal tile reference should update");
        assertFalse(startTile.getEntities().contains(troop), "Unit should be removed from the starting tile");
        assertTrue(targetTile.getEntities().contains(troop), "Unit should be added to the target tile");
    }

    @Test
    void moveTroopUnit_OutOfRangeTarget_FailsAndDoesNotMove() {
        Tile startTile = gameMap.getTile(0, 0);
        Tile outOfRangeTile = gameMap.getTile(0, 5); // Infantry range is 2

        TroopUnit troop = new TroopUnit(TroopType.Infantry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);
        GridPositionHelper.placeEntity(troop, startTile);

        boolean success = movementManager.moveTroopUnit(troop, outOfRangeTile);

        assertFalse(success, "Movement out of range should fail");
        assertFalse(troop.hasMovedThisTurn(), "Unit should not be marked as moved");
        assertEquals(startTile, troop.getTile(), "Unit should remain on the starting tile");
    }

    @Test
    void moveTroopUnit_OntoEnemyCity_TriggersConquest() {
        Tile cityTile = gameMap.getTile(0, 1);
        City enemyCity = new City(cityTile, CityType.LEVEL_1);
        OwnershipHelper.transferCity(enemyCity, player2); // Belongs to P2
        GridPositionHelper.placeEntity(enemyCity, cityTile);

        Tile startTile = gameMap.getTile(0, 0);
        TroopUnit troop = new TroopUnit(TroopType.Infantry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(troop, player1); // Belongs to P1
        GridPositionHelper.placeEntity(troop, startTile);

        assertEquals(player2, enemyCity.getOwner(), "City should initially belong to Player 2");

        boolean success = movementManager.moveTroopUnit(troop, cityTile);

        assertTrue(success, "Movement onto city should succeed");
        assertEquals(player1, enemyCity.getOwner(), "City ownership should transfer to Player 1 upon entry");
        assertTrue(player1.getCities().contains(enemyCity), "City should be added to Player 1's list");
    }

    @Test
    void moveTroopUnit_OntoEnemyTroop_FailsAndDoesNotMove() {
        Tile startTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit movingTroop = new TroopUnit(TroopType.Infantry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(movingTroop, player1);
        GridPositionHelper.placeEntity(movingTroop, startTile);

        TroopUnit enemyTroop = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(enemyTroop, player2);
        GridPositionHelper.placeEntity(enemyTroop, targetTile);

        boolean success = movementManager.moveTroopUnit(movingTroop, targetTile);

        assertFalse(success, "Movement onto an enemy troop's tile must fail");
        assertFalse(movingTroop.hasMovedThisTurn(), "Unit's state should not be updated to 'moved'");
        assertEquals(startTile, movingTroop.getTile(), "Unit must remain on its starting tile");
        assertTrue(targetTile.getEntities().contains(enemyTroop), "Enemy unit must remain undisturbed on the target tile");
    }

    @Test
    void moveTroopUnit_OnEnemyTurn_FailsAndDoesNotMove() {
        Tile startTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit troop = new TroopUnit(TroopType.Infantry, startTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);
        GridPositionHelper.placeEntity(troop, startTile);

        movementManager.onTurnStart(player2);

        boolean success = movementManager.moveTroopUnit(troop, targetTile);

        assertFalse(success, "Movement must be rejected if it is the enemy's turn");
        assertFalse(troop.hasMovedThisTurn(), "Unit's state should not be updated to 'moved'");
        assertEquals(startTile, troop.getTile(), "Unit must remain on its starting tile");
        assertFalse(targetTile.getEntities().contains(troop), "Unit must not enter the target tile");
    }
}