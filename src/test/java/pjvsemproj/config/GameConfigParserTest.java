package pjvsemproj.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigParserTest {

    private GameConfigParser parser;

    @BeforeEach
    void setUp() {
        parser = new GameConfigParser();
    }

    @Test
    void parseLevelConfig_ValidPolymorphicEntities_LoadsCorrectSubclasses() throws Exception {
        URL resourceUrl = getClass().getResource("/valid_config.json");
        assertNotNull(resourceUrl, "Test fixture valid_config.json must exist in src/test/resources/");
        String absolutePath = Paths.get(resourceUrl.toURI()).toString();

        GameDTO result = parser.parseLevelConfig(absolutePath);

        // Verify Basic Data based on your valid_config.json structure
        assertEquals(5, result.mapWidth, "Map width must match the fixture");
        assertEquals("Vasya", result.currentPlayerName, "Current player name must match the fixture");
        assertEquals(2, result.players.size(), "Must load exactly 2 players");
        assertEquals(4, result.entities.size(), "Must load exactly 4 entities");

        // Verify Custom Deserialization for CityDTO
        assertInstanceOf(CityDTO.class, result.entities.get(0), "First entity must be deserialized as a CityDTO");
        CityDTO city = (CityDTO) result.entities.get(0);
        assertEquals("vasya's city", city.id, "City ID must be extracted correctly");
        assertEquals("LEVEL_1", city.cityLevel, "City level must be extracted correctly");
        assertEquals(0, city.upgradePrice, "Transient fields must initialize to their default values (0)");

        // Verify Custom Deserialization for TroopUnitDTO
        assertInstanceOf(TroopUnitDTO.class, result.entities.get(1), "Second entity must be deserialized as a TroopUnitDTO");
        TroopUnitDTO troop = (TroopUnitDTO) result.entities.get(1);
        assertEquals(1, troop.hp, "Troop HP must be extracted correctly");
        assertFalse(troop.hasMovedThisTurn, "Troop movement flag must be extracted correctly");
    }

    @Test
    void parseLevelConfig_WithUnknownJsonProperties_IgnoresThemAndParsesValidData() throws Exception {
        URL resourceUrl = getClass().getResource("/config_with_unknowns.json");
        assertNotNull(resourceUrl, "Test fixture config_with_unknowns.json must exist in src/test/resources/");
        String absolutePath = Paths.get(resourceUrl.toURI()).toString();

        GameDTO parsedDto = parser.parseLevelConfig(absolutePath);

        assertNotNull(parsedDto, "Parser must successfully return a DTO despite unknown fields");
        assertEquals(12, parsedDto.mapWidth, "Known property 'mapWidth' must be extracted correctly");
        assertEquals(12, parsedDto.mapHeight, "Known property 'mapHeight' must be extracted correctly");
        assertEquals("Alice", parsedDto.currentPlayerName, "Known property 'currentPlayerName' must be extracted correctly");
    }

    @Test
    void parseLevelConfig_FileNotFound_ThrowsRuntimeException() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> parser.parseLevelConfig("this_file_does_not_exist.json"),
                "Parser must throw an exception if the configuration file is missing"
        );

        assertTrue(exception.getMessage().contains("Failed to load config file"),
                "Exception message must clearly indicate the failure reason");
    }

    @Test
    void saveLevelConfig_ValidDTO_WritesCorrectJsonToFile(@TempDir Path tempDir) throws Exception {
        // Build a minimal valid GameDTO
        List<PlayerDTO> players = List.of(new PlayerDTO("Ivan", 500));
        GameDTO dummyGame = new GameDTO(15, 20, new ArrayList<>(), players, "Ivan");

        Path file = tempDir.resolve("test_save.json");

        parser.saveLevelConfig(dummyGame, file.toString());

        assertTrue(Files.exists(file), "The save file must be physically created on disk");

        String savedJson = Files.readString(file);
        assertTrue(savedJson.contains("\"mapWidth\": 15"), "Saved JSON must contain the correct map width");
        assertTrue(savedJson.contains("\"mapHeight\": 20"), "Saved JSON must contain the correct map height");
        assertTrue(savedJson.contains("\"currentPlayerName\": \"Ivan\""), "Saved JSON must contain the correct current player name");
    }
}