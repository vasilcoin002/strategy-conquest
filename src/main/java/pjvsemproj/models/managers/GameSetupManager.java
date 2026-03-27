package pjvsemproj.models.managers;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.maps.GameMap;
import pjvsemproj.models.maps.Tile;

public class GameSetupManager {

    public Game setupStandardMatch(GameMap map, Player p1, Player p2) {
        City p1City = new City(CityType.LEVEL_1);
        p1City.setOwner(p1);
        p1.addCity(p1City);

        Tile p1CityTile = map.getTile(1, 2);
        if (p1CityTile != null) {
            // TODO move to class which handles relations
            p1City.setTile(p1CityTile);
            p1CityTile.addEntity(p1City);
        }

        TroopUnit p1StartUnit = new TroopUnit(TroopType.Militia, p1City);
        // TODO move to class which handles relations
        p1.addTroopUnit(p1StartUnit);

        Tile p1UnitTile = map.getTile(1, 1);
        if (p1UnitTile != null) {
            boolean success = p1UnitTile.addEntity(p1StartUnit);
            if (success) {
                p1StartUnit.setTile(p1UnitTile);
            }
        }


        City p2City = new City(CityType.LEVEL_1);
        p2City.setOwner(p2);
        p2.addCity(p2City);

        // Opposite side of map
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        Tile p2CityTile = map.getTile(mapWidth - 2, mapHeight - 3);
        if (p2CityTile != null) {
            // TODO move to class which handles relations
            p2City.setTile(p2CityTile);
            p2CityTile.addEntity(p2City);
        }

        TroopUnit p2StartUnit = new TroopUnit(TroopType.Militia, p2City);
        // TODO move to class which handles relations
        p2.addTroopUnit(p2StartUnit);

        Tile p2UnitTile = map.getTile(mapWidth - 2, mapHeight - 2);
        if (p2UnitTile != null) {
            boolean success = p2UnitTile.addEntity(p2StartUnit);
            if (success) {
                p2StartUnit.setTile(p2UnitTile);
            }
        }


        City neutralCity = new City(CityType.LEVEL_1); // No owner
        Tile neutralTile = map.getTile(mapWidth / 2, mapHeight / 2);
        if (neutralTile != null) {
            // TODO move to class which handles relations
            neutralTile.addEntity(neutralCity);
            neutralCity.setTile(neutralTile);
        }

        Game game = new Game(map);
        game.addPlayer(p1);
        game.addPlayer(p2);

        return game;
    }
}