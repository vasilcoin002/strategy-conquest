package pjvsemproj.config;

import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;

import static pjvsemproj.models.game.GameConstants.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameConfigValidator {

    public void validate(GameDTO gameDTO) {
        if (gameDTO == null) {
            throw new InvalidGameConfigException("Game configuration cannot be null.");
        }

        validateMap(gameDTO);
        validatePlayers(gameDTO);
        validateEntities(gameDTO);
        validateWinnerStatus(gameDTO);
    }

    private void validateMap(GameDTO gameDTO) {
        if (gameDTO.mapWidth < MIN_MAP_WIDTH || gameDTO.mapHeight < MIN_MAP_HEIGHT) {
            throw new InvalidGameConfigException(String.format("Map must be at least %dx%d. Provided: %dx%d",
                    MIN_MAP_WIDTH, MIN_MAP_HEIGHT, gameDTO.mapWidth, gameDTO.mapHeight));
        }
    }

    private void validatePlayers(GameDTO gameDTO) {
        if (gameDTO.players == null || gameDTO.players.size() != PLAYERS_COUNT) {
            throw new InvalidGameConfigException("Game must have exactly " + PLAYERS_COUNT + " players.");
        }

        boolean currentPlayerFound = isCurrentPlayerFound(gameDTO);

        if (!currentPlayerFound) {
            throw new InvalidGameConfigException("Current player '" + gameDTO.currentPlayerName + "' is not in the player list.");
        }
    }

    private static boolean isCurrentPlayerFound(GameDTO gameDTO) {
        boolean currentPlayerFound = false;
        for (PlayerDTO player : gameDTO.players) {
            if (player.name == null || player.name.isBlank()) {
                throw new InvalidGameConfigException("Player name cannot be empty.");
            }
            if (player.balance < 0) {
                throw new InvalidGameConfigException("Player '" + player.name + "' has a negative balance: " + player.balance);
            }
            if (player.name.equals(gameDTO.currentPlayerName)) {
                currentPlayerFound = true;
            }
        }
        return currentPlayerFound;
    }

    // TODO add validation if there is no entities with the same id
    private void validateEntities(GameDTO gameDTO) {
        if (gameDTO.entities == null || gameDTO.entities.isEmpty()) return;

        Set<String> validPlayerNames = gameDTO.players.stream()
                .map(p -> p.name)
                .collect(Collectors.toSet());

        Set<String> occupiedCityTiles = new HashSet<>();
        Set<String> occupiedTroopTiles = new HashSet<>();

        for (EntityDTO entity : gameDTO.entities) {
            validateEntityBounds(entity, gameDTO.mapWidth, gameDTO.mapHeight);
            validateEntityOwner(entity, validPlayerNames);

            String tileKey = entity.x + "," + entity.y;

            if (entity instanceof CityDTO cityDTO) {
                validateCity(cityDTO, tileKey, occupiedCityTiles);
            } else if (entity instanceof TroopUnitDTO troopDTO) {
                validateTroop(troopDTO, tileKey, occupiedTroopTiles);
            } else {
                validateBaseEntity(entity);
            }
        }
    }

    private void validateEntityBounds(EntityDTO entity, int mapWidth, int mapHeight) {
        if (entity.x < 0 || entity.x >= mapWidth || entity.y < 0 || entity.y >= mapHeight) {
            throw new InvalidGameConfigException(String.format("Entity '%s' is out of map bounds at (%d,%d)",
                    entity.entityType, entity.x, entity.y));
        }
    }

    private void validateEntityOwner(EntityDTO entity, Set<String> validPlayerNames) {
        if (entity.ownerName != null && !validPlayerNames.contains(entity.ownerName)) {
            throw new InvalidGameConfigException(String.format("Entity '%s' at (%d,%d) belongs to an unknown player: %s",
                    entity.entityType, entity.x, entity.y, entity.ownerName));
        }
    }

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
        if (!Arrays.asList(ENTITY_TYPES).contains(entity.entityType)) {
            throw new InvalidGameConfigException(String.format("Entity at (%d,%d) has an invalid entityType: %s",
                    entity.x, entity.y, entity.entityType));
        }
    }

    private void validateWinnerStatus(GameDTO gameDTO) {
        if (gameDTO.entities == null) return;

        Set<String> playersWithCities = new HashSet<>();

        for (EntityDTO entity : gameDTO.entities) {
            if (entity instanceof CityDTO && entity.ownerName != null) {
                playersWithCities.add(entity.ownerName);
            }
        }

        if (playersWithCities.size() < PLAYERS_COUNT) {
            throw new InvalidGameConfigException("Cannot load game: The game is already over (one or more players have no cities).");
        }
    }
}