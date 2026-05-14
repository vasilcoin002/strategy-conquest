package pjvsemproj.models.game.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {

    private GameMap map;

    @BeforeEach
    void setUp() {
        map = new GameMap(10, 10);
    }

    @Test
    void testCoordinateValidation() {
        // Valid bounds
        assertTrue(map.isValidCoordinate(0, 0));
        assertTrue(map.isValidCoordinate(9, 9));
        assertTrue(map.isValidCoordinate(5, 5));

        // Invalid bounds (Negative)
        assertFalse(map.isValidCoordinate(-1, 0));
        assertFalse(map.isValidCoordinate(0, -1));

        // Invalid bounds (Exceeding max)
        assertFalse(map.isValidCoordinate(10, 0));
        assertFalse(map.isValidCoordinate(0, 10));
    }

    @Test
    void testTileBlockingLogic() {
        Tile tile = map.getTile(0, 0);
        assertFalse(tile.isBlocked(), "Empty tile should not be blocked");

        City city = new City(CityType.LEVEL_1); // Passable entity
        tile.addEntity(city);
        assertFalse(tile.isBlocked(), "Tile with only a city should not be blocked");

        TroopUnit troop = new TroopUnit(TroopType.Militia, null, false, false); // Impassable entity
        tile.addEntity(troop);
        assertTrue(tile.isBlocked(), "Tile containing a troop should be blocked");
    }

    @Test
    public void testTilesCoordinates() {
        int expectedX = 5;
        int expectedY = 3;

        Tile tile = map.getTiles()[expectedX][expectedY];

        assertEquals(expectedX, tile.getX());
        assertEquals(expectedY, tile.getY());
    }
}
