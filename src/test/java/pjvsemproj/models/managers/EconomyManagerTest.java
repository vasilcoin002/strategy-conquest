package pjvsemproj.models.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import static org.junit.jupiter.api.Assertions.*;

public class EconomyManagerTest {

    private Player player1;
    private Player player2;
    private EconomyManager economyManager;
    private City player1City;
    private Tile cityTile;

    @BeforeEach
    void setUp() {
        player1 = new HumanPlayer("Player 1", 100);
        player2 = new HumanPlayer("Player 2", 100);
        economyManager = new EconomyManager(player1);

        cityTile = new Tile(0, 0);
        player1City = new City(cityTile, CityType.LEVEL_1);

        OwnershipHelper.transferCity(player1City, player1);
        GridPositionHelper.placeEntity(player1City, cityTile);
    }

    @Test
    public void countProducedGold_MixedLevels_ReturnsCorrectSum() {
        City level2City = new City(new Tile(1, 1), CityType.LEVEL_2);
        OwnershipHelper.transferCity(level2City, player1);

        // LEVEL_1 produces 15, LEVEL_2 produces 30
        int expectedGold = CityType.LEVEL_1.goldProducedPerRound + CityType.LEVEL_2.goldProducedPerRound;
        int producedGold = economyManager.countProducedGold();

        assertEquals(expectedGold, producedGold);
    }

    @Test
    public void countProducedGold_MultipleCitiesOfLevel1_ReturnsCorrectSum() {
        // Player already has 1 city from setup
        OwnershipHelper.transferCity(new City(new Tile(1, 1), CityType.LEVEL_1), player1);
        OwnershipHelper.transferCity(new City(new Tile(1, 2), CityType.LEVEL_1), player1);

        int expectedProducedGold = 3 * CityType.LEVEL_1.goldProducedPerRound;
        int producedGold = economyManager.countProducedGold();

        assertEquals(expectedProducedGold, producedGold);
    }

    @Test
    public void onTurnStart_AddsProducedGoldToBalance() {
        int initialBalance = player1.getBalance();
        int expectedIncome = player1City.getGoldProducedPerRound();

        economyManager.onTurnStart(player1);

        assertEquals(initialBalance + expectedIncome, player1.getBalance());
    }

    @Test
    public void upgradeCity_WithSufficientBalance_UpgradesAndSpendsGold() {
        int initialBalance = player1.getBalance();
        int upgradePrice = player1City.getUpgradePrice();

        boolean success = economyManager.upgradeCity(player1City);

        assertTrue(success, "City should be successfully upgraded");
        assertEquals(CityType.LEVEL_2, player1City.getCurrentLevel(), "City level should advance");
        assertEquals(initialBalance - upgradePrice, player1.getBalance(), "Gold should be deducted");
    }

    @Test
    public void upgradeCity_WithInsufficientBalance_FailsAndDoesNotSpend() {
        player1.setBalance(10); // Not enough for LEVEL_1 upgrade price (40)

        boolean success = economyManager.upgradeCity(player1City);

        assertFalse(success, "Upgrade should fail due to low balance");
        assertEquals(CityType.LEVEL_1, player1City.getCurrentLevel(), "City level should not change");
        assertEquals(10, player1.getBalance(), "Gold should not be spent");
    }

    @Test
    public void upgradeCity_NotOwnedByPlayer_Fails() {
        City enemyCity = new City(new Tile(5, 5), CityType.LEVEL_1);
        OwnershipHelper.transferCity(enemyCity, player2);

        boolean success = economyManager.upgradeCity(enemyCity);

        assertFalse(success, "Cannot upgrade an enemy's city");
        assertEquals(CityType.LEVEL_1, enemyCity.getCurrentLevel(), "Enemy city should not be upgraded");
    }

    @Test
    public void upgradeCity_AlreadyMaxLevel_Fails() {
        City maxLevelCity = new City(new Tile(2, 2), CityType.LEVEL_3);
        OwnershipHelper.transferCity(maxLevelCity, player1);
        int initialBalance = player1.getBalance();

        boolean success = economyManager.upgradeCity(maxLevelCity);

        assertFalse(success, "Upgrade should fail because city is max level");
        assertEquals(initialBalance, player1.getBalance(), "Gold should not be deducted");
    }

    @Test
    public void buyTroopUnit_WithSufficientBalance_PlacesTroopAndSpendsGold() {
        int initialBalance = player1.getBalance();
        int price = TroopType.Militia.getPrice();

        boolean success = economyManager.buyTroopUnit(TroopType.Militia, player1City);

        assertTrue(success, "Should successfully buy a unit");
        assertEquals(initialBalance - price, player1.getBalance(), "Gold should be spent");
        assertEquals(1, player1.getTroops().size(), "Player should own the new troop");
        assertTrue(cityTile.getEntities().stream().anyMatch(e -> e instanceof TroopUnit), "Troop should be placed on the city tile");
    }

    @Test
    public void buyTroopUnit_WithInsufficientFunds_Fails() {
        player1.setBalance(5); // Not enough for Militia (20)

        boolean success = economyManager.buyTroopUnit(TroopType.Militia, player1City);

        assertFalse(success, "Should fail to buy unit due to low funds");
        assertEquals(0, player1.getTroops().size(), "No troop should be added");
    }

    @Test
    public void buyTroopUnit_NotOwnedByPlayer_Fails() {
        City enemyCity = new City(new Tile(5, 5), CityType.LEVEL_1);
        OwnershipHelper.transferCity(enemyCity, player2);

        boolean success = economyManager.buyTroopUnit(TroopType.Militia, enemyCity);

        assertFalse(success, "Cannot buy a troop in an enemy city");
        assertEquals(0, player1.getTroops().size(), "No troop should be added to player 1");
    }

    @Test
    public void buyTroopUnit_CityTileBlocked_Fails() {
        // Place an existing troop on the city tile to block it
        TroopUnit existingTroop = new TroopUnit(TroopType.Militia, cityTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(existingTroop, player1);
        GridPositionHelper.placeEntity(existingTroop, cityTile);

        int initialBalance = player1.getBalance();

        boolean success = economyManager.buyTroopUnit(TroopType.Infantry, player1City);

        assertFalse(success, "Should fail to buy unit because the spawn tile is blocked");
        assertEquals(initialBalance, player1.getBalance(), "Gold should not be deducted");
        assertEquals(1, player1.getTroops().size(), "Only the pre-existing troop should exist");
    }
}