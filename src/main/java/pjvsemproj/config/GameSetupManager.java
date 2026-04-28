package pjvsemproj.config;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;

/**
 * Responsible for initializing game state.
 *
 * Can create test scenarios or load game from configuration files.
 */
public class GameSetupManager {

    private final GameConfigParser parser;

    public GameSetupManager() {
        this.parser = new GameConfigParser();
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


//        City neutralCity = new City(CityType.LEVEL_1); // No owner
//        // Middle of the map
//        Tile neutralTile = map.getTile(map.getWidth() / 2, map.getHeight() / 2);
//        GridPositionHelper.placeEntity(neutralCity, neutralTile);

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        return game;
    }

    /**
     * Reads a level file and creates a game from those data.
     */
    public Game loadGame(String levelFilePath) {
        return null;
    }
}