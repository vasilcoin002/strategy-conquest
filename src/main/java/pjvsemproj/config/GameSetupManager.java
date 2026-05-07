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
import java.util.Map;

// TODO write unit tests
/**
 * Responsible for initializing game state.
 *
 * Can create test scenarios or load game from configuration files.
 */
public class GameSetupManager {

    private final GameConfigParser parser;
    private final GameConfigValidator validator;

    public GameSetupManager() {
        this.parser = new GameConfigParser();
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
        OwnershipHelper.addTroopUnitToPlayer(p1StartUnit, p1);
        GridPositionHelper.placeEntity(p1StartUnit, p1CityTile);


        City p2City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p2City, p2);
        // Opposite side of the map
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
     * Loads a game for Single Player.
     * The player matching 'localClientName' is human, everyone else is a Bot.
     */
    public Game loadLocalGame(String levelFilePath, String localClientName) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);
        validator.validate(gameDTO); // Assuming you extracted validation
        return createGameFromDTO(gameDTO, localClientName, true);
    }

    /**
     * Loads a game for Multiplayer.
     * All players are instantiated as HumanPlayers.
     */
    public Game loadNetworkGame(String levelFilePath) {
        GameDTO gameDTO = parser.parseLevelConfig(levelFilePath);
        validator.validate(gameDTO);
        return createGameFromDTO(gameDTO, null, false);
    }

    // TODO refactor this method
    // TODO add default values
    //  (for example if there is no currentPlayerName, then assign the first player as the current one and so on)
    private Game createGameFromDTO(GameDTO dto, String localClientName, boolean isLocalVsBot) {
        GameMap map = new GameMap(dto.mapWidth, dto.mapHeight);
        Game game = new Game(map);
        Map<String, Player> loadedPlayers = new HashMap<>();

        for (PlayerDTO playerDTO : dto.players) {
            Player player;

            if (isLocalVsBot && !playerDTO.name.equals(localClientName)) {
                player = new BotPlayer(playerDTO.name, playerDTO.balance);
            } else {
                player = new HumanPlayer(playerDTO.name, playerDTO.balance);
            }

            game.addPlayer(player);
            loadedPlayers.put(player.getName(), player);
        }

        Player currentPlayer = loadedPlayers.get(dto.currentPlayerName);
        if (currentPlayer != null) {
            game.setCurrentPlayer(currentPlayer);
        } else {
            System.err.println("Warning: Current player from save not found. Defaulting to Player 1.");
            game.setCurrentPlayer(game.getPlayers().getFirst());
        }

        for (EntityDTO entityDTO : dto.entities) {
            Tile tile = map.getTile(entityDTO.x, entityDTO.y);
            Player owner = loadedPlayers.get(entityDTO.ownerName);

            if (entityDTO instanceof CityDTO cityDTO) {
                CityType type = CityType.valueOf(cityDTO.cityLevel);
                City city = new City(cityDTO.id, tile, type);

                GridPositionHelper.placeEntity(city, tile);
                OwnershipHelper.transferCity(city, owner);

            } else if (entityDTO instanceof TroopUnitDTO troopDTO) {
                TroopType type = TroopType.valueOf(troopDTO.entityType);

                TroopUnit troop = new TroopUnit(
                        troopDTO.id, type, tile,
                        troopDTO.hasMovedThisTurn, troopDTO.hasAttackedThisTurn
                );

                troop.setHealth(troopDTO.hp);
                troop.setHasMovedThisTurn(troopDTO.hasMovedThisTurn);
                troop.setHasAttackedThisTurn(troopDTO.hasAttackedThisTurn);

                GridPositionHelper.placeEntity(troop, tile);
                OwnershipHelper.addTroopUnitToPlayer(troop, owner);
            }
        }

        return game;
    }
}