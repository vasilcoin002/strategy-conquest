package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.helpers.GridPositionHelper;
import pjvsemproj.models.managers.helpers.OwnershipHelper;
import pjvsemproj.models.maps.GameMap;
import pjvsemproj.models.maps.Tile;

public class GameSetupManager {
    public Game setupStandardMatch(GameMap map, Player p1, Player p2) {
        City p1City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p1City, p1);
        Tile p1CityTile = map.getTile(1, 1);
        GridPositionHelper.placeEntity(p1City, p1CityTile);

        TroopUnit p1StartUnit = new TroopUnit(TroopType.Militia, p1City);
        OwnershipHelper.transferTroopUnit(p1StartUnit, p1);
        GridPositionHelper.placeEntity(p1StartUnit, p1CityTile);


        City p2City = new City(CityType.LEVEL_1);
        OwnershipHelper.transferCity(p2City, p2);
        // Opposite side of the map
        Tile p2CityTile = map.getTile(map.getWidth() - 2, map.getHeight() - 2);
        GridPositionHelper.placeEntity(p2City, p2CityTile);

        TroopUnit p2StartUnit = new TroopUnit(TroopType.Militia, p2City);
        OwnershipHelper.transferTroopUnit(p2StartUnit, p2);
        GridPositionHelper.placeEntity(p2StartUnit, p2CityTile);


        City neutralCity = new City(CityType.LEVEL_1); // No owner
        // Middle of the map
        Tile neutralTile = map.getTile(map.getWidth() / 2, map.getHeight() / 2);
        GridPositionHelper.placeEntity(neutralCity, neutralTile);

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        return game;
    }
}