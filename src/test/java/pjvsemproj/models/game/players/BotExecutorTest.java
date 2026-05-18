package pjvsemproj.models.game.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.models.services.LocalGameService;

import static org.junit.jupiter.api.Assertions.*;

public class BotExecutorTest {

    private Game game;
    private GameMap map;
    private BotPlayer bot;
    private HumanPlayer human;
    private LocalGameService service;

    @BeforeEach
    void setUp() {
        map = new GameMap(10, 10);
        game = new Game(map);

        bot = new BotPlayer("Bot", 100);
        human = new HumanPlayer("Human", 100);

        game.addPlayer(bot);
        game.addPlayer(human);
        game.setCurrentPlayer(bot);

        service = new LocalGameService(game);
    }

    @Test
    void botTurn_WhenBotHasCityAndEnoughGold_ShouldBuyTroop() {
        bot.setBalance(10);

        City botCity = new City("bot-city", map.getTile(1, 1), CityType.LEVEL_1);
        OwnershipHelper.transferCity(botCity, bot);
        GridPositionHelper.placeEntity(botCity, map.getTile(1, 1));

        City humanCity = new City("human-city", map.getTile(8, 8), CityType.LEVEL_1);
        OwnershipHelper.transferCity(humanCity, human);
        GridPositionHelper.placeEntity(humanCity, map.getTile(8, 8));

        assertEquals(0, bot.getTroops().size());

        BotExecutor executor = new BotExecutor(service);
        executor.playTurnActionsOnly();

        assertEquals(1, bot.getTroops().size());
        assertEquals(0, bot.getBalance());

        TroopUnit boughtUnit = bot.getTroops().getFirst();
        assertEquals(botCity.getTile(), boughtUnit.getTile());
        assertTrue(botCity.getTile().getEntities().contains(boughtUnit));
    }

    @Test
    void botTurn_WhenBotHasFourTroopsAndUpgradeableCity_ShouldUpgradeCity() {
        bot.setBalance(40);

        City botCity = new City("bot-city", map.getTile(1, 1), CityType.LEVEL_1);
        OwnershipHelper.transferCity(botCity, bot);
        GridPositionHelper.placeEntity(botCity, map.getTile(1, 1));

        City humanCity = new City("human-city", map.getTile(8, 8), CityType.LEVEL_1);
        OwnershipHelper.transferCity(humanCity, human);
        GridPositionHelper.placeEntity(humanCity, map.getTile(8, 8));

        for (int i = 0; i < 4; i++) {
            TroopUnit troop = new TroopUnit(
                    "bot-unit-" + i,
                    TroopType.Militia,
                    map.getTile(2 + i, 1),
                    true,
                    true
            );

            OwnershipHelper.addTroopUnitToPlayer(troop, bot);
            GridPositionHelper.placeEntity(troop, map.getTile(2 + i, 1));
        }

        assertEquals(CityType.LEVEL_1, botCity.getCityType());

        BotExecutor executor = new BotExecutor(service);
        executor.playTurnActionsOnly();

        assertEquals(CityType.LEVEL_2, botCity.getCityType());
        assertEquals(0, bot.getBalance());
    }

    @Test
    void botTurn_WhenEnemyIsInAttackRange_ShouldAttackEnemyTroop() {
        bot.setBalance(0);

        City botCity = new City("bot-city", map.getTile(1, 1), CityType.LEVEL_1);
        OwnershipHelper.transferCity(botCity, bot);
        GridPositionHelper.placeEntity(botCity, map.getTile(1, 1));

        City humanCity = new City("human-city", map.getTile(8, 8), CityType.LEVEL_1);
        OwnershipHelper.transferCity(humanCity, human);
        GridPositionHelper.placeEntity(humanCity, map.getTile(8, 8));

        TroopUnit botTroop = new TroopUnit(
                "bot-unit",
                TroopType.Militia,
                map.getTile(2, 2),
                false,
                false
        );

        TroopUnit enemyTroop = new TroopUnit(
                "enemy-unit",
                TroopType.Militia,
                map.getTile(3, 2),
                false,
                false
        );

        enemyTroop.setHealth(20);

        OwnershipHelper.addTroopUnitToPlayer(botTroop, bot);
        GridPositionHelper.placeEntity(botTroop, map.getTile(2, 2));

        OwnershipHelper.addTroopUnitToPlayer(enemyTroop, human);
        GridPositionHelper.placeEntity(enemyTroop, map.getTile(3, 2));

        BotExecutor executor = new BotExecutor(service);
        executor.playTurnActionsOnly();

        assertTrue(
                enemyTroop.getHealth() < 20 || !human.getTroops().contains(enemyTroop),
                "Enemy troop should be damaged or removed after bot attack."
        );

        assertTrue(botTroop.hasAttackedThisTurn());
        assertTrue(botTroop.hasMovedThisTurn());
    }

    @Test
    void botTurn_WhenNoAffordableTroop_ShouldNotBuyUnit() {
        bot.setBalance(0);

        City botCity = new City("bot-city", map.getTile(1, 1), CityType.LEVEL_1);
        OwnershipHelper.transferCity(botCity, bot);
        GridPositionHelper.placeEntity(botCity, map.getTile(1, 1));

        City humanCity = new City("human-city", map.getTile(8, 8), CityType.LEVEL_1);
        OwnershipHelper.transferCity(humanCity, human);
        GridPositionHelper.placeEntity(humanCity, map.getTile(8, 8));

        assertEquals(0, bot.getTroops().size());

        BotExecutor executor = new BotExecutor(service);
        executor.playTurnActionsOnly();

        assertEquals(0, bot.getTroops().size());
        assertEquals(0, bot.getBalance());
    }

    @Test
    void botTurn_WhenEnemyIsNearby_ShouldMoveTroopCloserToTarget() {
        bot.setBalance(0);

        City botCity = new City("bot-city", map.getTile(1, 1), CityType.LEVEL_1);
        OwnershipHelper.transferCity(botCity, bot);
        GridPositionHelper.placeEntity(botCity, map.getTile(1, 1));

        City humanCity = new City("human-city", map.getTile(8, 8), CityType.LEVEL_1);
        OwnershipHelper.transferCity(humanCity, human);
        GridPositionHelper.placeEntity(humanCity, map.getTile(8, 8));

        TroopUnit botTroop = new TroopUnit(
                "bot-unit",
                TroopType.Militia,
                map.getTile(2, 2),
                false,
                false
        );

        OwnershipHelper.addTroopUnitToPlayer(botTroop, bot);
        GridPositionHelper.placeEntity(botTroop, map.getTile(2, 2));

        int oldDistance = Math.abs(botTroop.getTile().getX() - humanCity.getTile().getX()) + Math.abs(botTroop.getTile().getY() - humanCity.getTile().getY());

        BotExecutor executor = new BotExecutor(service);
        executor.playTurnActionsOnly();

        int newDistance = Math.abs(botTroop.getTile().getX() - humanCity.getTile().getX()) + Math.abs(botTroop.getTile().getY() - humanCity.getTile().getY());

        assertTrue(newDistance < oldDistance);
        assertTrue(botTroop.hasMovedThisTurn());
    }
}