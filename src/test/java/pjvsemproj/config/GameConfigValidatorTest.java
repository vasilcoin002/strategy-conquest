package pjvsemproj.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigValidatorTest {

    private GameConfigValidator validator;

    @BeforeEach
    void setUp() {
        validator = new GameConfigValidator();
    }

    private GameDTO createValidBaseDTO() {
        GameDTO dto = new GameDTO();
        dto.mapWidth = 10;
        dto.mapHeight = 10;
        dto.currentPlayerName = "Player 1";
        dto.players = Arrays.asList(
                new PlayerDTO("Player 1", 100),
                new PlayerDTO("Player 2", 100)
        );
        dto.entities = new ArrayList<>();
        return dto;
    }

    @Test
    void validate_ValidConfig_PassesWithoutException() {
        GameDTO dto = createValidBaseDTO();

        // Add valid entities inside map bounds
        dto.entities.add(new CityDTO("c1", "City", 1, 1, "Player 1",
                CityType.LEVEL_1.name(), 40,
                true, 15, true));
        dto.entities.add(new TroopUnitDTO(
                "t1", TroopType.Infantry.name(), 1, 2, "Player 1",
                TroopType.Infantry.maxHealth, TroopType.Infantry.maxHealth,
                TroopType.Infantry.minDamage, TroopType.Infantry.maxDamage,
                false, false
        ));
        dto.entities.add(new CityDTO("c2", "City", 8, 8, "Player 2",
                CityType.LEVEL_1.name(), 40,
                true, 15, true));
        dto.entities.add(new TroopUnitDTO(
                "t2", TroopType.Infantry.name(), 8, 7, "Player 2",
                TroopType.Infantry.maxHealth, TroopType.Infantry.maxHealth,
                TroopType.Infantry.minDamage, TroopType.Infantry.maxDamage,
                false, false
        ));

        assertDoesNotThrow(() -> validator.validate(dto),
                "A completely valid configuration should not throw any exceptions.");
    }

    @Test
    void validate_NegativeMapDimensions_ThrowsException() {
        GameDTO dto = createValidBaseDTO();
        dto.mapWidth = -5; // Invalid width

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject maps with negative or zero width.");
    }

    @Test
    void validate_EntityOutOfBoundsNegative_ThrowsException() {
        GameDTO dto = createValidBaseDTO();

        // Troop placed at negative X
        dto.entities.add(new TroopUnitDTO("t1", TroopType.Infantry.name(), -1, 5, "Player 1", 100, 100, 10, 20, false, false));

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject entities placed at negative coordinates.");
    }

    @Test
    void validate_EntityOutOfBoundsExceedsMapSize_ThrowsException() {
        GameDTO dto = createValidBaseDTO();

        dto.entities.add(new TroopUnitDTO("t1", TroopType.Infantry.name(), 10, 5, "Player 1", 100, 100, 10, 20, false, false));

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject entities placed beyond the map width/height boundaries.");
    }

    @Test
    void validate_OverlappingTroops_ThrowsException() {
        GameDTO dto = createValidBaseDTO();

        // Two troops placed on the exact same coordinate (5, 5)
        dto.entities.add(new TroopUnitDTO("t1", TroopType.Infantry.name(), 5, 5, "Player 1", 100, 100, 10, 20, false, false));
        dto.entities.add(new TroopUnitDTO("t2", TroopType.Cavalry.name(), 5, 5, "Player 2", 100, 100, 10, 20, false, false));

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject configurations where multiple impassable troops occupy the same tile.");
    }

    @Test
    void validate_EmptyPlayerList_ThrowsException() {
        GameDTO dto = createValidBaseDTO();
        dto.players = new ArrayList<>(); // 0 players

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject a game that has no players.");
    }

    @Test
    void validate_DuplicatePlayerNames_ThrowsException() {
        GameDTO dto = createValidBaseDTO();

        dto.players = Arrays.asList(
                new PlayerDTO("Clone", 100),
                new PlayerDTO("Clone", 100)
        );
        dto.currentPlayerName = "Clone";

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject configurations where players share the same name.");
    }

    @Test
    void validate_MissingEntityId_ThrowsException() {
        GameDTO dto = createValidBaseDTO();

        // Entity with a null ID
        dto.entities.add(new CityDTO(null, "City", 5, 5, "Player 1", CityType.LEVEL_1.name(), 40, true, 15, true));

        assertThrows(InvalidGameConfigException.class, () -> validator.validate(dto),
                "Validator must reject entities that do not have a unique identifier.");
    }

    @Test
    void validate_LoadedInvalidConfigFile_ThrowsException() throws Exception {
        GameConfigParser parser = new GameConfigParser();
        java.net.URL resourceUrl = getClass().getResource("/invalid_config.json");
        assertNotNull(resourceUrl, "Test fixture invalid_config.json must exist in src/test/resources/");
        String absolutePath = java.nio.file.Paths.get(resourceUrl.toURI()).toString();

        GameDTO invalidDto = parser.parseLevelConfig(absolutePath);

        InvalidGameConfigException exception = assertThrows(
                InvalidGameConfigException.class,
                () -> validator.validate(invalidDto),
                "Validator must reject this configuration because it contains multiple domain violations."
        );
    }
}