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
 * Responsible for initializing game state.
 * <p>
 * Can create test scenarios or load game from configuration files.
 */
public class GameSetupManager {

    private final GameConfigParser parser;
    private final GameConfigSanitizer sanitizer;
    private final GameConfigValidator validator;

    public GameSetupManager() {
        this.parser = new GameConfigParser();
        this.sanitizer = new GameConfigSanitizer();
        this.validator = new GameConfigValidator();
    }

    /**
     * Sets up a match with 1 city and 1 Militia unit for each player
     */
    public Game setupTestMatch(GameMap map, Player p1, Player p2) {
        City p1City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p1City, p1);
        Tile p1CityTile = map.getTile(1, 1);
        GridPositionHelper.placeEntity(p1City, p1CityTile);

        TroopUnit p1StartUnit = new TroopUnit(TroopType.Militia, p1City);
        p1StartUnit.setHasMovedThisTurn(false);
        p1StartUnit.setHasAttackedThisTurn(false);
        OwnershipHelper.addTroopUnitToPlayer(p1StartUnit, p1);
        GridPositionHelper.placeEntity(p1StartUnit, p1CityTile);

        // Opposite side of the map
        City p2City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p2City, p2);
        Tile p2CityTile = map.getTile(map.getWidth() - 2, map.getHeight() - 2);
        GridPositionHelper.placeEntity(p2City, p2CityTile);

        TroopUnit p2StartUnit = new TroopUnit(TroopType.Militia, p2City);
        p2StartUnit.setHasMovedThisTurn(false);
        p2StartUnit.setHasAttackedThisTurn(false);
        OwnershipHelper.addTroopUnitToPlayer(p2StartUnit, p2);
        GridPositionHelper.placeEntity(p2StartUnit, p2CityTile);

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        game.setCurrentPlayer(p1);

        return game;
    }

    /**
     * Loads a game for Single Player.
     * The player matching 'localClientName' is human, everyone else is a Bot.
     */
    public Game loadLocalGame(String levelFilePath, String localClientName) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);

        sanitizer.sanitize(gameDTO);
        validator.validate(gameDTO);

        validateClientNameExists(gameDTO, localClientName);

        return createGameFromDTO(gameDTO, localClientName, true);
    }

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
     * Loads a game for Multiplayer.
     * All players are instantiated as HumanPlayers.
     */
    public Game loadNetworkGame(String levelFilePath) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);

        sanitizer.sanitize(gameDTO);
        validator.validate(gameDTO);

        return createGameFromDTO(gameDTO, null, false);
    }

    /**
     * Creates game from parsed game settings
     */
    private Game createGameFromDTO(GameDTO dto, String localClientName, boolean isLocalVsBot) {
        GameMap map = new GameMap(dto.mapWidth, dto.mapHeight);
        Game game = new Game(map);

        Map<String, Player> loadedPlayers = loadPlayers(game, dto.players, localClientName, isLocalVsBot);

        setCurrentPlayer(game, loadedPlayers, dto.currentPlayerName);
        loadEntities(map, dto.entities, loadedPlayers);

        return game;
    }

    /**
     * Loads parsed players to the game object
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
     * Loads current player to the game object by their name
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
     * Loads parsed entities to the game object
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
     * Loads parsed city to game
     */
    private void spawnCity(CityDTO cityDTO, Tile tile, Player owner) {
        CityType type = CityType.valueOf(cityDTO.cityLevel);
        City city = new City(cityDTO.id, tile, type);

        GridPositionHelper.placeEntity(city, tile);
        OwnershipHelper.transferCity(city, owner);
    }

    /**
     * Loads parsed troop unit to game
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

    public Game createNetworkGameFromDTO(GameDTO dto, String localClientName) {
        return createGameFromDTO(dto, localClientName, false);
    }
}