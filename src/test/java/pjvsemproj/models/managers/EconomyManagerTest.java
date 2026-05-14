package pjvsemproj.models.managers;

import org.junit.jupiter.api.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EconomyManagerTest {

    private static Player player1;
    private static Player player2;
    private static EconomyManager economyManager;

    @BeforeEach
    void setUp() {
//        player1 = new HumanPlayer("TestPlayer", 0);
//        player2 = new HumanPlayer("TestPlayer", 0);
//        economyManager = new EconomyManager(player1);
//
//        Tile cityTile = new Tile(0, 0);
//        player1city = new City(cityTile, CityType.LEVEL_1);
//
//        OwnershipHelper.transferCity(player1city, player1);
//        GridPositionHelper.placeEntity(player1city, cityTile);
    }

    @Test
    public void testCountProducedGold_testMatch() {

    }

    @Test
    public void testCountProducedGold_3citiesOfLevel1() {
        int expectedProducedGold = 45;

        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);
        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);
        int producedGold = economyManager.countProducedGold();

        assertEquals(expectedProducedGold, producedGold);
    }

    @Test
    public void testUpgradeCity_byCurrentPlayer_isUpgraded() {

    }

    @Test
    public void testUpgradeCity_byCurrentPlayerWithLowGold_isNotUpgraded() {

    }

    @Test
    public void testUpgradeCity_EnemyCityByCurrentPlayer_isNotUpgraded() {

    }

    @Test
    public void testUpgradeCity_byEnemy_isNotUpgraded() {

    }

    @Test
    public void testBuyTroopUnit_byCurrentPlayer_isBought() {

    }

    @Test
    public void testBuyTroopUnit_byCurrentPlayerWithLowGold_isNotBought() {

    }

    @Test
    public void testBuyTroopUnit_onEnemyCityByCurrentPlayer_isNotBought() {

    }

    @Test
    public void testBuyTroopUnit_byEnemy_isNotBought() {

    }
}
