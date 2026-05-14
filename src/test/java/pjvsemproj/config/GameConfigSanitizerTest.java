package pjvsemproj.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigSanitizerTest {

    private GameConfigSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new GameConfigSanitizer();
    }

    @Test
    void sanitize_MissingMapDimensions_InjectsDefaultDimensions() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = null;
        dto.mapHeight = null;
        dto.players = new ArrayList<>();
        dto.entities = new ArrayList<>();

        sanitizer.sanitize(dto);

        assertNotNull(dto.mapWidth, "Sanitizer must replace null mapWidth");
        assertNotNull(dto.mapHeight, "Sanitizer must replace null mapHeight");

        // Assuming your ConfigDefaultValues has these constants. Update names if necessary.
        assertEquals(ConfigDefaultValues.DEFAULT_MAP_WIDTH, dto.mapWidth, "Width must match default constant");
        assertEquals(ConfigDefaultValues.DEFAULT_MAP_HEIGHT, dto.mapHeight, "Height must match default constant");
    }

    @Test
    void sanitize_MissingPlayerBalance_InjectsDefaultBalance() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = 10;
        dto.mapHeight = 10;
        dto.entities = new ArrayList<>();

        // Player with null balance
        PlayerDTO p1 = new PlayerDTO("Player 1", 0);
        p1.balance = null;
        dto.players = Arrays.asList(p1);

        sanitizer.sanitize(dto);

        assertNotNull(p1.balance, "Sanitizer must replace null balance");
        assertEquals(ConfigDefaultValues.DEFAULT_INIT_BALANCE, p1.balance, "Balance must match default constant");
    }

    @Test
    void sanitize_MissingTroopFlags_InjectsDefaultTurnStates() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = 10;
        dto.mapHeight = 10;
        dto.players = new ArrayList<>();
        dto.entities = new ArrayList<>();

        // Troop with null boolean flags
        TroopUnitDTO troop = new TroopUnitDTO(
                "t1", "Infantry", 0, 0, "P1",
                100, 100, 10, 20, false, false
        );
        troop.hasMovedThisTurn = null;
        troop.hasAttackedThisTurn = null;
        dto.entities.add(troop);

        sanitizer.sanitize(dto);

        assertNotNull(troop.hasMovedThisTurn, "Sanitizer must replace null movement flag");
        assertNotNull(troop.hasAttackedThisTurn, "Sanitizer must replace null attack flag");

        assertFalse(troop.hasMovedThisTurn, "Troop should default to not having moved");
        assertFalse(troop.hasAttackedThisTurn, "Troop should default to not having attacked");
    }

    @Test
    void sanitize_MissingCurrentPlayerName_DefaultsToFirstPlayer() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = 10;
        dto.mapHeight = 10;
        dto.currentPlayerName = null;
        dto.entities = new ArrayList<>();

        PlayerDTO p1 = new PlayerDTO("Alice", 100);
        PlayerDTO p2 = new PlayerDTO("Bob", 100);
        dto.players = Arrays.asList(p1, p2);

        sanitizer.sanitize(dto);

        assertNotNull(dto.currentPlayerName, "Sanitizer must assign a current player name if missing");
        assertEquals("Alice", dto.currentPlayerName, "Should default to the first player in the list");
    }

    @Test
    void sanitize_FullyPopulatedDTO_DoesNotOverwriteExistingValues() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = 50;
        dto.mapHeight = 60;
        dto.currentPlayerName = "Bob";

        PlayerDTO p1 = new PlayerDTO("Alice", 999); // Non-default balance
        PlayerDTO p2 = new PlayerDTO("Bob", 5);
        dto.players = Arrays.asList(p1, p2);

        TroopUnitDTO troop = new TroopUnitDTO(
                "t1", "Infantry", 0, 0, "Alice",
                100, 100, 10, 20, true, true // Non-default flags
        );
        dto.entities = Arrays.asList(troop);

        sanitizer.sanitize(dto);

        assertEquals(50, dto.mapWidth, "Valid map width should not be overwritten");
        assertEquals(60, dto.mapHeight, "Valid map height should not be overwritten");
        assertEquals(999, p1.balance, "Valid balance should not be overwritten");
        assertTrue(troop.hasMovedThisTurn, "Valid boolean flag should not be overwritten");
        assertEquals("Bob", dto.currentPlayerName, "Valid current player should remain untouched");
    }
}