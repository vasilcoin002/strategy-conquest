package pjvsemproj.models.managers;

import org.junit.jupiter.api.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.OwnershipHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EconomyManagerTest {

    private static Player player1;
    private static EconomyManager economyManager;

    @BeforeAll
    public static void init() {
        player1 = new HumanPlayer("Vasya", 0);

        economyManager = new EconomyManager(player1);
    }

    @Test
    @Order(0)
    public void countProducedGold_standardMatch_returns15() {
        int expectedProducedGold = 15;

        economyManager.setCurrentPlayer(player1);
        int producedGold = economyManager.countProducedGold();

        assertEquals(expectedProducedGold, producedGold);
    }

    @Test
    @Order(1)
    public void countProducedGold_3citiesOfLevel1_returns45() {
        int expectedProducedGold = 45;

        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);
        OwnershipHelper.transferCity(new City(CityType.LEVEL_1), player1);
        int producedGold = economyManager.countProducedGold();

        assertEquals(expectedProducedGold, producedGold);
    }
}
