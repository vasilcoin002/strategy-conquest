package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.helpers.GridPositionHelper;
import pjvsemproj.models.maps.GameMap;
import pjvsemproj.models.maps.Tile;

public class GameSetupManager {

    // TODO create interface Ownable
    // TODO add TroopUnit -> Owner relationship (now it's not working)
    // TODO move connecting ownable entities with owner to helper class
    public Game setupStandardMatch(GameMap map, Player p1, Player p2) {
        City p1City = new City(CityType.LEVEL_1);
        p1City.setOwner(p1);
        p1.addCity(p1City);

        Tile p1CityTile = map.getTile(1, 2);
        GridPositionHelper.placeEntity(p1City, p1CityTile);

        TroopUnit p1StartUnit = new TroopUnit(TroopType.Militia, p1City);
        // TODO move to class which handles owning relations
        p1.addTroopUnit(p1StartUnit);

        Tile p1UnitTile = map.getTile(1, 1);
        GridPositionHelper.placeEntity(p1StartUnit, p1UnitTile);


        City p2City = new City(CityType.LEVEL_1);
        p2City.setOwner(p2);
        p2.addCity(p2City);

        // Opposite side of map
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        Tile p2CityTile = map.getTile(mapWidth - 2, mapHeight - 3);
        GridPositionHelper.placeEntity(p2City, p2CityTile);

        TroopUnit p2StartUnit = new TroopUnit(TroopType.Militia, p2City);
        // TODO move to class which handles relations
        p2.addTroopUnit(p2StartUnit);

        Tile p2UnitTile = map.getTile(mapWidth - 2, mapHeight - 2);
        GridPositionHelper.placeEntity(p2StartUnit, p2UnitTile);


        City neutralCity = new City(CityType.LEVEL_1); // No owner
        Tile neutralTile = map.getTile(mapWidth / 2, mapHeight / 2);
        GridPositionHelper.placeEntity(neutralCity, neutralTile);

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        return game;
    }
}