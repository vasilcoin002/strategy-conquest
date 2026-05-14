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
import pjvsemproj.models.managers.utils.OwnershipHelper;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConquestManagerTest {

    private Player player1;
    private Player player2;
    private ConquestManager conquestManager;

    @BeforeEach
    void setUp() {
        player1 = new HumanPlayer("Player 1", 100);
        player2 = new HumanPlayer("Player 2", 100);
        conquestManager = new ConquestManager(Arrays.asList(player1, player2), player1);
    }

    @Test
    void conquerCity_EnemyCity_TransfersOwnershipAndUpdatesLists() {
        // Setup a city owned by Player 2
        City enemyCity = new City(new Tile(0, 0), CityType.LEVEL_1);
        OwnershipHelper.transferCity(enemyCity, player2);

        // Setup an attacking troop owned by Player 1
        TroopUnit attacker = new TroopUnit(TroopType.Infantry, new Tile(0, 0), false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);

        assertEquals(player2, enemyCity.getOwner(), "Initial owner should be Player 2");
        assertTrue(player2.getCities().contains(enemyCity), "City should initially be in Player 2's list");

        // Execute conquest
        conquestManager.conquerCity(attacker, enemyCity);

        // Assertions
        assertEquals(player1, enemyCity.getOwner(), "City ownership must transfer to Player 1");
        assertTrue(player1.getCities().contains(enemyCity), "City must be added to Player 1's active city list");
        assertFalse(player2.getCities().contains(enemyCity), "City must be permanently removed from Player 2's city list");
    }

    @Test
    void winnerExists_MultiplePlayersWithCities_ReturnsFalse() {
        // Give both players a city
        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);
        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player2);

        // Assert that the game is still ongoing
        assertFalse(conquestManager.winnerExists(), "Win condition should not trigger when multiple players own cities");
    }

    @Test
    void winnerExists_SinglePlayerWithCities_ReturnsTrue() {
        // Give only Player 1 a city (Player 2 has 0)
        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);

        // Assert that the game recognizes a winner
        assertTrue(conquestManager.winnerExists(), "Win condition must trigger if only one player remains with cities");
    }

    @Test
    void conquerCity_ResultsInWin_NotifiesWinListeners() {
        Tile battlegroundTile = new Tile(5, 5); // Explicitly define the coordinate

        // Setup the game state where both players have exactly 1 city
        City player1City = new City(new Tile(0, 0), CityType.LEVEL_1);
        OwnershipHelper.transferCity(player1City, player1);

        City player2City = new City(battlegroundTile, CityType.LEVEL_1); // Place P2's city here
        OwnershipHelper.transferCity(player2City, player2);

        // Setup a custom test listener to capture the event
        class TestWinListener implements IWinListener {
            boolean wasNotified = false;
            Player reportedWinner = null;

            @Override
            public void onWin(Player winner) {
                this.wasNotified = true;
                this.reportedWinner = winner;
            }
        }

        TestWinListener testListener = new TestWinListener();
        conquestManager.addWinListener(testListener);

        // Player 1 attacks and conquers Player 2's ONLY city
        // The attacker MUST be on the exact same tile to pass the new validation
        TroopUnit attacker = new TroopUnit(TroopType.Infantry, battlegroundTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);

        conquestManager.conquerCity(attacker, player2City);

        // Assertions
        assertTrue(testListener.wasNotified, "Listener must be notified immediately when the win condition is met");
        assertEquals(player1, testListener.reportedWinner, "Listener must receive the correct winning player instance");
    }

    @Test
    void conquerCity_AttackerOnDifferentTile_ThrowsException() {
        Tile cityTile = new Tile(0, 0);
        Tile attackerTile = new Tile(0, 1); // Attacker is one tile away

        City enemyCity = new City(cityTile, CityType.LEVEL_1);
        OwnershipHelper.transferCity(enemyCity, player2);

        TroopUnit attacker = new TroopUnit(TroopType.Infantry, attackerTile, false, false);
        OwnershipHelper.addTroopUnitToPlayer(attacker, player1);

        // Attempting to conquer from a different tile should trigger our guard clause
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> conquestManager.conquerCity(attacker, enemyCity),
                "Expected conquerCity to throw an exception when attacker is not on the city tile"
        );

        // Ensure ownership did not accidentally transfer
        assertEquals(player2, enemyCity.getOwner(), "City must remain owned by Player 2");
    }
}