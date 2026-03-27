package pjvsemproj.models.maps;

public class GameMap {
    private final Tile[][] tiles;

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

    public Tile[][] getTiles() {
        return tiles;
    }

    public Tile getTile(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return tiles[x][y];
        }
        return null;
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

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
