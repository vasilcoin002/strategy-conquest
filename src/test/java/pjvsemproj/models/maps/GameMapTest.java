package pjvsemproj.models.maps;

import org.junit.jupiter.api.Test;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameMapTest {
    @Test
    public void constructor_10x10_tilesHaveRightCoordinates() {
        int expectedX = 5;
        int expectedY = 3;

        GameMap map = new GameMap(10, 10);
        Tile tile = map.getTiles()[expectedX][expectedY];

        assertTrue(expectedX == tile.getX() && expectedY == tile.getY());
    }
}
