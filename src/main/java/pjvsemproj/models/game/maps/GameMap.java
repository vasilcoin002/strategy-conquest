package pjvsemproj.models.game.maps;


/**
 * Represents the game world as a 2D grid of tiles.
 *
 * Responsible for:
 * - storing map structure
 * - providing access to tiles
 * - validating coordinates
 *
 * The map is immutable in size after creation.
 */
public class GameMap {
    private final Tile[][] tiles;
    /**
     * Creates a new game map with specified dimensions.
     *
     * Initializes all tiles in the grid.
     *
     * @param width number of columns (X dimension)
     * @param height number of rows (Y dimension)
     * @throws IllegalArgumentException if width or height < 2
     */

    // TODO Test initializing with negative values, 0 and 1
    public GameMap(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("map width and height must be ≥ 2");
        }

        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.tiles[x][y] = new Tile(x, y);
            }
        }
    }
    /**
     * Returns the entire tile grid.
     *
     * @return 2D array of tiles
     */
    public Tile[][] getTiles() {
        return tiles;
    }
    /**
     * Returns tile at given coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return tile if coordinates are valid, otherwise null
     */
    public Tile getTile(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return tiles[x][y];
        }
        return null;
    }
    /**
     * Checks whether given coordinates are within map bounds.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if coordinates are valid
     */
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Checks whether the tile at given coordinates is blocked.
     *
     * A tile is considered blocked if it contains a non-passable entity.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if tile is blocked
     */
    public boolean isTileBlocked(int x, int y) {
        return tiles[x][y].isBlocked();
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }
}
