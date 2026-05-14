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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CombatManagerTest {

    private GameMap gameMap;
    private Player player1;
    private Player player2;
    private CombatManager combatManager;

    @BeforeEach
    void setUp() {
        gameMap = new GameMap(10, 10);
        player1 = new HumanPlayer("Player 1", 100);
        player2 = new HumanPlayer("Player 2", 100);
        combatManager = new CombatManager(gameMap, player1);
    }

    @Test
    void onTurnStart_ResetsAttackFlag_ForCurrentPlayerTroops() {
        TroopUnit troop = new TroopUnit(TroopType.Infantry, gameMap.getTile(0, 0), false, true); // hasAttacked = true
        OwnershipHelper.addTroopUnitToPlayer(troop, player1);

        assertTrue(troop.hasAttackedThisTurn(), "Troop should initially have the attacked flag set");

        combatManager.onTurnStart(player1);

        assertFalse(troop.hasAttackedThisTurn(), "Attacked flag should be reset at the start of the turn");
    }

    @Test
    void onTurnStart_TroopsInFriendlyCities_HealBy25PercentAndCapAtMaxHealth() {
        Tile cityTile = gameMap.getTile(2, 2);

        // Setup friendly city
        City friendlyCity = new City(cityTile, CityType.LEVEL_1);
        OwnershipHelper.transferCity(friendlyCity, player1);
        GridPositionHelper.placeEntity(friendlyCity, cityTile);

        // Setup friendly troop on the city tile
        TroopUnit stationedTroop = new TroopUnit(TroopType.Militia, cityTile, false, false); // Max HP = 20
        OwnershipHelper.addTroopUnitToPlayer(stationedTroop, player1);
        GridPositionHelper.placeEntity(stationedTroop, cityTile);

        // Damage the troop heavily (Take 8 damage, HP becomes 12)
        stationedTroop.takeDamage(8);
        assertEquals(12, stationedTroop.getHealth(), "Troop should initially be damaged to 12 HP");

        // Turn 1: Prove the 25% step increment
        // 25% of 20 Max HP = 5. HP should go from 12 to 17.
        combatManager.onTurnStart(player1);
        assertEquals(17, stationedTroop.getHealth(), "Turn 1: Troop should heal exactly 25% of its Max HP (5 HP)");

        // Turn 2: Prove the Maximum HP cap
        // 17 + 5 = 22, but it must cap at 20.
        combatManager.onTurnStart(player1);
        assertEquals(20, stationedTroop.getHealth(), "Turn 2: Troop's health must cap strictly at its Max HP (20)");

        // Turn 3: Prove it stays stable at max
        combatManager.onTurnStart(player1);
        assertEquals(20, stationedTroop.getHealth(), "Turn 3: Troop should remain perfectly stable at Max HP");
    }

    @Test
    void getAttackableTroops_EnemiesInRange_ReturnsCorrectSet() {
        Tile startTile = gameMap.getTile(5, 5);
        TroopUnit attacker = new TroopUnit(TroopType.Artillery, startTile, false, false); // Artillery Range = 3
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, startTile);

        // Target 1: Enemy in range (distance 2)
        Tile target1Tile = gameMap.getTile(5, 7);
        TroopUnit enemyInRange = new TroopUnit(TroopType.Militia, target1Tile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(enemyInRange, player2);
        GridPositionHelper.placeEntity(enemyInRange, target1Tile);

        // Target 2: Enemy out of range (distance 4)
        Tile target2Tile = gameMap.getTile(9, 5);
        TroopUnit enemyOutOfRange = new TroopUnit(TroopType.Militia, target2Tile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(enemyOutOfRange, player2);
        GridPositionHelper.placeEntity(enemyOutOfRange, target2Tile);

        // Target 3: Friendly unit in range (distance 1)
        Tile friendlyTile = gameMap.getTile(5, 6);
        TroopUnit friendlyUnit = new TroopUnit(TroopType.Infantry, friendlyTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(friendlyUnit, player1);
        GridPositionHelper.placeEntity(friendlyUnit, friendlyTile);

        Set<TroopUnit> attackableTroops = combatManager.getAttackableTroops(attacker);

        assertEquals(1, attackableTroops.size(), "Should only find exactly 1 attackable target");
        assertTrue(attackableTroops.contains(enemyInRange), "Enemy in range must be in the attackable set");
        assertFalse(attackableTroops.contains(enemyOutOfRange), "Enemy out of range must be excluded");
        assertFalse(attackableTroops.contains(friendlyUnit), "Friendly fire must be prevented");
    }

    @Test
    void getAttackableTroops_AlreadyAttacked_ReturnsEmptySet() {
        Tile startTile = gameMap.getTile(5, 5);
        TroopUnit attacker = new TroopUnit(TroopType.Infantry, startTile, false, true); // hasAttacked = true
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, startTile);

        Tile targetTile = gameMap.getTile(5, 6);
        TroopUnit enemy = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(enemy, player2);
        GridPositionHelper.placeEntity(enemy, targetTile);

        Set<TroopUnit> attackableTroops = combatManager.getAttackableTroops(attacker);

        assertTrue(attackableTroops.isEmpty(), "A unit that has already attacked cannot see any attackable targets");
    }

    @Test
    void attackTroop_ValidTarget_AppliesDamageAndSetsFlags() {
        Tile attackerTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit attacker = new TroopUnit(TroopType.Infantry, attackerTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, attackerTile);

        TroopUnit target = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(target, player2);
        GridPositionHelper.placeEntity(target, targetTile);

        int initialTargetHp = target.getHealth();

        boolean success = combatManager.attackTroop(attacker, target);

        assertTrue(success, "Attack execution should return true");
        assertTrue(attacker.hasAttackedThisTurn(), "Attacker's hasAttackedThisTurn flag should be true");
        assertTrue(attacker.hasMovedThisTurn(), "Attacker's hasMovedThisTurn flag should be true (locks movement)");
        assertTrue(target.getHealth() < initialTargetHp, "Target should have taken damage");
    }

    @Test
    void attackTroop_LethalDamage_RemovesTroopFromBoardAndPlayer() {
        Tile attackerTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit attacker = new TroopUnit(TroopType.Artillery, attackerTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, attackerTile);

        TroopUnit target = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(target, player2);
        GridPositionHelper.placeEntity(target, targetTile);

        // Force target HP to 1 to guarantee a lethal blow
        target.setHealth(1);

        boolean success = combatManager.attackTroop(attacker, target);

        assertTrue(success, "Attack execution should return true");
        assertTrue(target.isDead(), "Target should be marked as dead");
        assertFalse(player2.getTroops().contains(target), "Dead troop must be removed from the owner's troop list");
        assertFalse(targetTile.getEntities().contains(target), "Dead troop must be removed from the game map tile");
    }

    @Test
    void attackTroop_InvalidTarget_ReturnsFalse() {
        Tile attackerTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 5); // Out of range for Infantry

        TroopUnit attacker = new TroopUnit(TroopType.Infantry, attackerTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, attackerTile);

        TroopUnit target = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(target, player2);
        GridPositionHelper.placeEntity(target, targetTile);

        boolean success = combatManager.attackTroop(attacker, target);

        assertFalse(success, "Attack should fail if the target is not within the attackable troops set");
        assertFalse(attacker.hasAttackedThisTurn(), "Attacker state should not be updated on a failed attack");
    }

    @Test
    void attackTroop_OnEnemyTurn_FailsAndDoesNotExecute() {
        Tile attackerTile = gameMap.getTile(0, 0);
        Tile targetTile = gameMap.getTile(0, 1);

        TroopUnit attacker = new TroopUnit(TroopType.Infantry, attackerTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);
        GridPositionHelper.placeEntity(attacker, attackerTile);

        TroopUnit target = new TroopUnit(TroopType.Militia, targetTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(target, player2);
        GridPositionHelper.placeEntity(target, targetTile);

        int initialTargetHp = target.getHealth();

        combatManager.onTurnStart(player2);

        boolean success = combatManager.attackTroop(attacker, target);

        assertFalse(success, "Attack must be rejected if it is the enemy's turn");
        assertFalse(attacker.hasAttackedThisTurn(), "Attacker's state should not update to 'attacked'");
        assertEquals(initialTargetHp, target.getHealth(), "Target must not take any damage");
    }
}