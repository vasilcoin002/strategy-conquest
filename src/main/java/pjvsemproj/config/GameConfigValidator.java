package pjvsemproj.config;

import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static pjvsemproj.models.game.GameConstants.*;

/**
 * Enforces the structural and logical invariants of a loaded game configuration.
 * <p>
 * This stateless utility acts as a strict gatekeeper before a raw {@link GameDTO}
 * is transformed into active domain models. It guarantees that map boundaries, player
 * ownership, entity constraints (such as valid HP ranges and non-overlapping tile logic),
 * and overarching game states are mathematically sound.
 * <p>
 * By validating DTOs directly, it prevents malformed save files or corrupted network
 * payloads from reaching the core game engine.
 */
public class GameConfigValidator {

    /**
     * Executes a full validation pass on the provided game configuration.
     * @param gameDTO The data transfer object representing the entire game state.
     * @throws InvalidGameConfigException if the configuration violates any core game rules,
     * contains conflicting entity placements, or is null.
     */
    public void validate(GameDTO gameDTO) {
        if (gameDTO == null) {
            throw new InvalidGameConfigException("Game configuration cannot be null.");
        }

        validateMap(gameDTO);
        validatePlayers(gameDTO);
        validateEntities(gameDTO);
        validateWinnerStatus(gameDTO);
    }

    /**
     * Verifies that the map dimensions meet the absolute minimum requirements
     * defined by the game's internal constants.
     */
    private void validateMap(GameDTO gameDTO) {
        if (gameDTO.mapWidth < MIN_MAP_WIDTH || gameDTO.mapHeight < MIN_MAP_HEIGHT) {
            throw new InvalidGameConfigException(String.format("Map must be at least %dx%d. Provided: %dx%d",
                    MIN_MAP_WIDTH, MIN_MAP_HEIGHT, gameDTO.mapWidth, gameDTO.mapHeight));
        }
    }

    /**
     * Validates player constraints, ensuring the exact required number of players exist,
     * their financial balances are valid, and the active turn player is correctly mapped.
     */
    private void validatePlayers(GameDTO gameDTO) {
        if (gameDTO.players == null || gameDTO.players.size() != PLAYERS_COUNT) {
            throw new InvalidGameConfigException("Game must have exactly " + PLAYERS_COUNT + " players.");
        }

        boolean currentPlayerFound = false;

        for (PlayerDTO player : gameDTO.players) {
            validateSinglePlayer(player); // Enforces name and balance rules

            if (player.name.equals(gameDTO.currentPlayerName)) {
                currentPlayerFound = true;
            }
        }

        if (!currentPlayerFound) {
            throw new InvalidGameConfigException("Current player '" + gameDTO.currentPlayerName + "' is not in the player list.");
        }
    }

    private void validateSinglePlayer(PlayerDTO player) {
        if (player.name == null || player.name.isBlank()) {
            throw new InvalidGameConfigException("Player name cannot be empty.");
        }
        if (player.balance < 0) {
            throw new InvalidGameConfigException("Player '" + player.name + "' has a negative balance: " + player.balance);
        }
    }

    /**
     * Iterates through all map entities to ensure they possess valid coordinates,
     * unique identifiers, and belong to recognized players. Also delegates specific
     * overlapping logic checks based on entity type.
     */
    private void validateEntities(GameDTO gameDTO) {
        if (gameDTO.entities == null || gameDTO.entities.isEmpty()) return;

        Set<String> validPlayerNames = gameDTO.players.stream()
                .map(p -> p.name)
                .collect(Collectors.toSet());

        Set<String> seenIds = new HashSet<>();
        Set<String> occupiedCityTiles = new HashSet<>();
        Set<String> occupiedTroopTiles = new HashSet<>();

        for (EntityDTO entity : gameDTO.entities) {
            validateGenericEntityRules(entity, gameDTO, validPlayerNames, seenIds);
            validateSpecificEntityRules(entity, occupiedCityTiles, occupiedTroopTiles);
        }
    }

    private void validateGenericEntityRules(EntityDTO entity, GameDTO gameDTO, Set<String> validPlayerNames, Set<String> seenIds) {
        // 1. Validate ID Uniqueness
        if (entity.id != null && !entity.id.isBlank()) {
            if (!seenIds.add(entity.id)) {
                throw new InvalidGameConfigException("Duplicate entity ID found in configuration: " + entity.id);
            }
        }

        // 2. Validate Bounds
        if (entity.x < 0 || entity.x >= gameDTO.mapWidth || entity.y < 0 || entity.y >= gameDTO.mapHeight) {
            throw new InvalidGameConfigException(String.format("Entity '%s' is out of map bounds at (%d,%d)",
                    entity.entityType, entity.x, entity.y));
        }

        // 3. Validate Owner Identity
        if (entity.ownerName != null && !validPlayerNames.contains(entity.ownerName)) {
            throw new InvalidGameConfigException(String.format("Entity '%s' at (%d,%d) belongs to an unknown player: %s",
                    entity.entityType, entity.x, entity.y, entity.ownerName));
        }
    }

    private void validateSpecificEntityRules(EntityDTO entity, Set<String> occupiedCityTiles, Set<String> occupiedTroopTiles) {
        String tileKey = entity.x + "," + entity.y;

        if (entity instanceof CityDTO cityDTO) {
            validateCity(cityDTO, tileKey, occupiedCityTiles);
        } else if (entity instanceof TroopUnitDTO troopDTO) {
            validateTroop(troopDTO, tileKey, occupiedTroopTiles);
        } else {
            validateBaseEntity(entity);
        }
    }

    /**
     * Ensures city levels map to valid enum states and prevents multiple cities
     * from overlapping on a single coordinate.
     */
    private void validateCity(CityDTO cityDTO, String tileKey, Set<String> occupiedCityTiles) {
        try {
            CityType.valueOf(cityDTO.cityLevel);
        } catch (IllegalArgumentException e) {
            throw new InvalidGameConfigException(String.format("Invalid CityType '%s' for City at (%d,%d)",
                    cityDTO.cityLevel, cityDTO.x, cityDTO.y));
        }

        if (!occupiedCityTiles.add(tileKey)) {
            throw new InvalidGameConfigException("Multiple cities found at tile (" + tileKey + ")");
        }
    }

    /**
     * Verifies troop states, ensuring their HP is strictly between 1 and their class maximum,
     * and guarantees that only one troop occupies a given grid tile.
     */
    private void validateTroop(TroopUnitDTO troopDTO, String tileKey, Set<String> occupiedTroopTiles) {
        TroopType type;
        try {
            type = TroopType.valueOf(troopDTO.entityType);
        } catch (IllegalArgumentException e) {
            throw new InvalidGameConfigException(String.format("Invalid TroopType '%s' at (%d,%d)",
                    troopDTO.entityType, troopDTO.x, troopDTO.y));
        }

        if (!occupiedTroopTiles.add(tileKey)) {
            throw new InvalidGameConfigException("Multiple troops found at tile (" + tileKey + ")");
        }

        int maxHp = type.maxHealth;
        if (troopDTO.hp <= 0 || troopDTO.hp > maxHp) {
            throw new InvalidGameConfigException(String.format("Troop '%s' at (%d,%d) has invalid HP (%d). Must be between 1 and %d.",
                    troopDTO.entityType, troopDTO.x, troopDTO.y, troopDTO.hp, maxHp));
        }
    }

    private void validateBaseEntity(EntityDTO entity) {
        if (!ENTITY_TYPES.contains(entity.entityType)) {
            throw new InvalidGameConfigException(String.format("Entity at (%d,%d) has an invalid entityType: %s",
                    entity.x, entity.y, entity.entityType));
        }
    }

    /**
     * Prevents the loading of games that have already reached a terminal state.
     * An active game requires that all participating players own at least one city.
     */
    private void validateWinnerStatus(GameDTO gameDTO) {
        if (gameDTO.entities == null) return;

        long playersWithCitiesCount = gameDTO.entities.stream()
                .filter(e -> e instanceof CityDTO && e.ownerName != null)
                .map(e -> e.ownerName)
                .distinct()
                .count();

        if (playersWithCitiesCount < PLAYERS_COUNT) {
            throw new InvalidGameConfigException("Cannot load game: The game is already over (one or more players have no cities).");
        }
    }
}