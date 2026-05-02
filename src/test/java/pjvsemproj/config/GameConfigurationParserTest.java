package pjvsemproj.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    void testParseLevelConfig_SuccessfullyLoadsPolymorphicEntities(@TempDir Path tempDir) throws IOException {
        // 1. Arrange: Create a fake JSON string with polymorphic entities
        String jsonContent = """
                {
                  "mapWidth": 10,
                  "mapHeight": 10,
                  "currentPlayerName": "Vasya",
                  "players": [
                    { "name": "Vasya", "balance": 100 }
                  ],
                  "entities": [
                    {
                      "entityType": "City",
                      "id": "city_1",
                      "x": 1,
                      "y": 1,
                      "ownerName": "Vasya",
                      "cityLevel": "LEVEL_1"
                    },
                    {
                      "entityType": "Militia",
                      "id": "troop_1",
                      "x": 2,
                      "y": 2,
                      "ownerName": "Vasya",
                      "hp": 15,
                      "hasMovedThisTurn": true
                    }
                  ]
                }
                """;

        // Write the fake JSON to our temporary test directory
        Path file = tempDir.resolve("test_load.json");
        Files.writeString(file, jsonContent);

        // 2. Act: Ask the parser to load it
        GameDTO result = parser.parseLevelConfig(file.toString());

        // 3. Assert: Verify the basic data
        assertEquals(10, result.mapWidth);
        assertEquals("Vasya", result.currentPlayerName);
        assertEquals(1, result.players.size());
        assertEquals(2, result.entities.size());

        // Verify the Custom Deserializer correctly built a CityDTO
        assertInstanceOf(CityDTO.class, result.entities.get(0));
        CityDTO city = (CityDTO) result.entities.get(0);
        assertEquals("city_1", city.id);
        assertEquals("LEVEL_1", city.cityLevel);

        // Verify Transient fields initialized to defaults (0) because they weren't in JSON
        assertEquals(0, city.upgradePrice);

        // Verify the Custom Deserializer correctly built a TroopUnitDTO
        assertInstanceOf(TroopUnitDTO.class, result.entities.get(1));
        TroopUnitDTO troop = (TroopUnitDTO) result.entities.get(1);
        assertEquals("troop_1", troop.id);
        assertEquals(15, troop.hp);
        assertTrue(troop.hasMovedThisTurn);
    }

    @Test
    void testSaveLevelConfig_SuccessfullyWritesJson(@TempDir Path tempDir) throws IOException {
        // 1. Arrange: Build a dummy GameDTO
        List<PlayerDTO> players = List.of(new PlayerDTO("Ivan", 500));
        GameDTO dummyGame = new GameDTO(15, 20, new ArrayList<>(), players, "Ivan");

        Path file = tempDir.resolve("test_save.json");

        // 2. Act: Save it
        parser.saveLevelConfig(dummyGame, file.toString());

        // 3. Assert: Read the file back as pure text and verify it contains our data
        assertTrue(Files.exists(file), "The save file should have been created.");
        String savedJson = Files.readString(file);

        assertTrue(savedJson.contains("\"mapWidth\": 15"));
        assertTrue(savedJson.contains("\"mapHeight\": 20"));
        assertTrue(savedJson.contains("\"currentPlayerName\": \"Ivan\""));
    }

    @Test
    void testParseLevelConfig_ThrowsExceptionOnBadPath() {
        // Act & Assert: Ensure the parser throws our custom RuntimeException if the file is missing
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            parser.parseLevelConfig("this_file_does_not_exist.json");
        });

        // Verify the error message is helpful to the developer
        assertTrue(exception.getMessage().contains("Failed to load config file"));
    }
}