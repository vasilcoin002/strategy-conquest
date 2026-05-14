package pjvsemproj.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSetupManagerTest {

    private GameSetupManager setupManager;

    @BeforeEach
    void setUp() {
        setupManager = new GameSetupManager();
    }

    @Test
    void setupTestMatch_CreatesSymmetricalGameState() {
        GameMap map = new GameMap(10, 10);
        Player p1 = new HumanPlayer("Player 1", 100);
        Player p2 = new HumanPlayer("Player 2", 100);

        Game game = setupManager.setupTestMatch(map, p1, p2);

        assertNotNull(game, "Game must be initialized");
        assertEquals(2, game.getPlayers().size(), "There must be exactly 2 players");
        assertEquals(p1, game.getCurrentPlayer(), "Player 1 should be set as the active player");

        // Verify P1 Setup (Top Left)
        assertEquals(1, p1.getCities().size(), "P1 should have 1 city");
        assertEquals(1, p1.getTroops().size(), "P1 should have 1 troop");
        Tile p1Tile = map.getTile(1, 1);
        assertTrue(p1Tile.getEntities().contains(p1.getCities().get(0)), "P1 City must be at (1,1)");

        // Verify P2 Setup (Bottom Right)
        assertEquals(1, p2.getCities().size(), "P2 should have 1 city");
        assertEquals(1, p2.getTroops().size(), "P2 should have 1 troop");
        Tile p2Tile = map.getTile(8, 8);
        assertTrue(p2Tile.getEntities().contains(p2.getCities().get(0)), "P2 City must be at (8,8)");
    }

    @Test
    void createGameFromDTO_ValidFlatDTO_CreatesCompleteGameState() {
        GameDTO mockDto = new GameDTO();
        mockDto.mapWidth = 10;
        mockDto.mapHeight = 10;
        mockDto.currentPlayerName = "Alice";

        PlayerDTO p1Dto = new PlayerDTO("Alice", 150);
        PlayerDTO p2Dto = new PlayerDTO("Bob", 200);
        mockDto.players = Arrays.asList(p1Dto, p2Dto);

        mockDto.entities = new ArrayList<>();

        CityDTO aliceCity = new CityDTO(
                "city-1", "City", 2, 2,
                "Alice", CityType.LEVEL_1.name(),
                40, true, 15,
                true
        );

        TroopUnitDTO aliceTroop = new TroopUnitDTO(
                "troop-1", TroopType.Infantry.name(), 2, 3, "Alice",
                100, 100, 10, 20,
                false, false
        );

        CityDTO neutralCity = new CityDTO(
                "city-neutral", "City", 5, 5,
                "Neutral", CityType.LEVEL_2.name(),
                80, true, 30,
                true
        );

        mockDto.entities.add(aliceCity);
        mockDto.entities.add(aliceTroop);
        mockDto.entities.add(neutralCity);

        Game game = setupManager.createGameFromDTO(mockDto, null, false);

        assertNotNull(game, "Game instance should be created");
        assertEquals(10, game.getMap().getWidth(), "Map width must match DTO");
        assertEquals(10, game.getMap().getHeight(), "Map height must match DTO");
        assertEquals("Alice", game.getCurrentPlayer().getName(), "Current player should be Alice");

        List<Player> players = game.getPlayers();
        assertEquals(2, players.size(), "Should load exactly 2 players");

        Player alice = players.stream().filter(p -> p.getName().equals("Alice")).findFirst().orElse(null);
        assertNotNull(alice, "Player Alice must exist");
        assertEquals(150, alice.getBalance(), "Alice's balance must match DTO");
        assertEquals(1, alice.getCities().size(), "Alice should own exactly 1 city from the flat list");
        assertEquals(1, alice.getTroops().size(), "Alice should own exactly 1 troop from the flat list");

        Tile aliceCityTile = game.getMap().getTile(2, 2);
        assertTrue(aliceCityTile.getEntities().contains(alice.getCities().get(0)), "Alice's city must be on the map");

        Tile neutralCityTile = game.getMap().getTile(5, 5);
        pjvsemproj.models.entities.cities.City mapNeutralCity =
                (pjvsemproj.models.entities.cities.City) neutralCityTile.getEntities().get(0);
        assertNull(mapNeutralCity.getOwner(), "Neutral city should not have an assigned player owner");
    }

    @Test
    void createGameFromDTO_LocalVsBot_CorrectlyInstantiatesBotPlayer() {
        GameDTO mockDto = new GameDTO();
        mockDto.mapWidth = 5;
        mockDto.mapHeight = 5;
        mockDto.currentPlayerName = "HumanPlayer";
        mockDto.players = Arrays.asList(
                new PlayerDTO("HumanPlayer", 100),
                new PlayerDTO("AI_Overlord", 100)
        );
        mockDto.entities = new ArrayList<>();

        Game game = setupManager.createGameFromDTO(mockDto, "HumanPlayer", true);

        Player local = game.getPlayers().stream().filter(p -> p.getName().equals("HumanPlayer")).findFirst().orElse(null);
        Player bot = game.getPlayers().stream().filter(p -> p.getName().equals("AI_Overlord")).findFirst().orElse(null);

        assertNotNull(local);
        assertNotNull(bot);

        assertInstanceOf(HumanPlayer.class, local, "The local client should be instantiated as a HumanPlayer");
        assertInstanceOf(BotPlayer.class, bot, "The opponent should be instantiated as a BotPlayer");
    }
}