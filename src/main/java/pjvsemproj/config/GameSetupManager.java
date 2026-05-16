package pjvsemproj.config;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the creation and initialization of active game sessions.
 * <p>
 * This manager acts as the bridge between raw, validated Data Transfer Objects (DTOs)
 * and the live domain models (e.g., {@link Game}, {@link GameMap}, {@link Player}).
 * It coordinates the parsing, sanitization, and validation pipeline before securely
 * hydrating the in-memory game state.
 */
public class GameSetupManager {

    private final GameConfigParser parser;
    private final GameConfigSanitizer sanitizer;
    private final GameConfigValidator validator;

    /**
     * Initializes the setup manager with its default configuration pipeline dependencies.
     */
    public GameSetupManager() {
        this.parser = new GameConfigParser();
        this.sanitizer = new GameConfigSanitizer();
        this.validator = new GameConfigValidator();
    }

    /**
     * Generates a hardcoded, minimal test scenario for two players.
     * Places one Level 1 City and one Militia unit for each player at opposite corners of the map.
     *
     * @param map The initialized game map to place entities on.
     * @param p1  The first player.
     * @param p2  The second player.
     * @return A fully initialized {@link Game} instance ready for testing.
     */
    public Game setupTestMatch(GameMap map, Player p1, Player p2) {
        City p1City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p1City, p1);
        Tile p1CityTile = map.getTile(1, 1);
        GridPositionHelper.placeEntity(p1City, p1CityTile);

        TroopUnit p1StartUnit = new TroopUnit(TroopType.Militia, p1City);
        p1StartUnit.setHasMovedThisTurn(false);
        OwnershipHelper.addTroopUnitToPlayer(p1StartUnit, p1);
        GridPositionHelper.placeEntity(p1StartUnit, p1CityTile);

        // Opposite side of the map
        City p2City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p2City, p2);
        Tile p2CityTile = map.getTile(map.getWidth() - 2, map.getHeight() - 2);
        GridPositionHelper.placeEntity(p2City, p2CityTile);

        TroopUnit p2StartUnit = new TroopUnit(TroopType.Militia, p2City);
        p2StartUnit.setHasMovedThisTurn(false);
        OwnershipHelper.addTroopUnitToPlayer(p2StartUnit, p2);
        GridPositionHelper.placeEntity(p2StartUnit, p2CityTile);

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        game.setCurrentPlayer(p1);

        return game;
    }

    /**
     * Executes the loading pipeline for a Single Player scenario.
     * The identity matching the {@code localClientName} is instantiated as a {@link HumanPlayer},
     * while all other players in the save file are instantiated as {@link BotPlayer}s.
     *
     * @param levelFilePath   The file path to the JSON game configuration.
     * @param localClientName The name of the local user loading the game.
     * @return A validated and fully hydrated {@link Game} instance.
     * @throws InvalidGameConfigException if the local client name is missing from the save file data.
     */
    public Game loadLocalGame(String levelFilePath, String localClientName) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);

        sanitizer.sanitize(gameDTO);
        validator.validate(gameDTO);

        validateClientNameExists(gameDTO, localClientName);

        return createGameFromDTO(gameDTO, localClientName, true);
    }

    /**
     * Validates that the executing client is actually a participant in the loaded configuration.
     * @param gameDTO    The parsed game configuration data.
     * @param clientName The name of the local client to verify.
     * @throws InvalidGameConfigException if the client name does not exist in the player list.
     */
    private void validateClientNameExists(GameDTO gameDTO, String clientName) {
        boolean nameFound = gameDTO.players.stream()
                .anyMatch(player -> player.name.equals(clientName));

        if (!nameFound) {
            throw new InvalidGameConfigException(
                    "Player name '" + clientName + "' was not found in the save file!"
            );
        }
    }

    /**
     * Executes the loading pipeline for a Multiplayer scenario.
     * All players defined in the configuration are instantiated as {@link HumanPlayer}s.
     *
     * @param levelFilePath The file path to the JSON game configuration.
     * @return A validated and fully hydrated {@link Game} instance.
     */
    public Game loadNetworkGame(String levelFilePath) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);

        sanitizer.sanitize(gameDTO);
        validator.validate(gameDTO);

        return createGameFromDTO(gameDTO, null, false);
    }

    /**
     * Core factory method that translates a sanitized and validated DTO into active domain models.
     *
     * @param dto             The guaranteed-safe game data transfer object.
     * @param localClientName The name of the client (if applicable).
     * @param isLocalVsBot    Flag indicating if non-client players should be mapped as Bots.
     * @return The fully constructed {@link Game} state.
     */
    Game createGameFromDTO(GameDTO dto, String localClientName, boolean isLocalVsBot) {
        GameMap map = new GameMap(dto.mapWidth, dto.mapHeight);
        Game game = new Game(map);

        Map<String, Player> loadedPlayers = loadPlayers(game, dto.players, localClientName, isLocalVsBot);

        setCurrentPlayer(game, loadedPlayers, dto.currentPlayerName);
        loadEntities(map, dto.entities, loadedPlayers);

        return game;
    }

    /**
     * Instantiates concrete Player objects based on network context and assigns them to the game.
     * @param game            The active game instance being constructed.
     * @param playerDTOs      The list of raw player data from the configuration.
     * @param localClientName The name of the local human client.
     * @param isLocalVsBot    Determines if opponents should be instantiated as AI bots.
     * @return A map linking player names to their newly instantiated {@link Player} objects.
     */
    private Map<String, Player> loadPlayers(Game game, List<PlayerDTO> playerDTOs, String localClientName, boolean isLocalVsBot) {
        Map<String, Player> loadedPlayers = new HashMap<>();

        for (PlayerDTO playerDTO : playerDTOs) {
            Player player;
            if (isLocalVsBot && !playerDTO.name.equals(localClientName)) {
                player = new BotPlayer(playerDTO.name, playerDTO.balance);
            } else {
                player = new HumanPlayer(playerDTO.name, playerDTO.balance);
            }

            game.addPlayer(player);
            loadedPlayers.put(player.getName(), player);
        }

        return loadedPlayers;
    }

    /**
     * Resolves and sets the active turn player. Fallbacks to the first player if the mapped name is missing.
     * @param game              The active game instance.
     * @param loadedPlayers     The map of currently instantiated players.
     * @param currentPlayerName The name of the player whose turn is currently active.
     */
    private void setCurrentPlayer(Game game, Map<String, Player> loadedPlayers, String currentPlayerName) {
        Player currentPlayer = loadedPlayers.get(currentPlayerName);
        if (currentPlayer != null) {
            game.setCurrentPlayer(currentPlayer);
        } else {
            System.err.println("Warning: Current player from save not found. Defaulting to Player 1.");
            game.setCurrentPlayer(game.getPlayers().getFirst());
        }
    }

    /**
     * Iterates through entity DTOs, determines their type, and delegates to specific spawn logic.
     * @param map           The initialized game map.
     * @param entityDTOs    The list of raw entity data from the configuration.
     * @param loadedPlayers The map of active players used to resolve ownership.
     */
    private void loadEntities(GameMap map, List<EntityDTO> entityDTOs, Map<String, Player> loadedPlayers) {
        for (EntityDTO entityDTO : entityDTOs) {
            Tile tile = map.getTile(entityDTO.x, entityDTO.y);
            Player owner = loadedPlayers.get(entityDTO.ownerName);

            if (entityDTO instanceof CityDTO cityDTO) {
                spawnCity(cityDTO, tile, owner);
            } else if (entityDTO instanceof TroopUnitDTO troopDTO) {
                spawnTroop(troopDTO, tile, owner);
            }
        }
    }

    /**
     * Instantiates a live City domain object and attaches it to the game grid and its owner.
     * @param cityDTO The validated data representing the city.
     * @param tile    The grid tile where the city will be placed.
     * @param owner   The player who controls the city.
     */
    private void spawnCity(CityDTO cityDTO, Tile tile, Player owner) {
        CityType type = CityType.valueOf(cityDTO.cityLevel);
        City city = new City(cityDTO.id, tile, type);

        GridPositionHelper.placeEntity(city, tile);
        OwnershipHelper.transferCity(city, owner);
    }

    /**
     * Instantiates a live TroopUnit domain object, restores its turn-state, and attaches it to the grid.
     * @param troopDTO The validated data representing the military unit.
     * @param tile     The grid tile where the unit will be placed.
     * @param owner    The player who commands the unit.
     */
    private void spawnTroop(TroopUnitDTO troopDTO, Tile tile, Player owner) {
        TroopType type = TroopType.valueOf(troopDTO.entityType);

        TroopUnit troop = new TroopUnit(
                troopDTO.id, type, tile,
                troopDTO.hasMovedThisTurn, troopDTO.hasAttackedThisTurn
        );

        troop.setHealth(troopDTO.hp);

        GridPositionHelper.placeEntity(troop, tile);
        OwnershipHelper.addTroopUnitToPlayer(troop, owner);
    }
}