package pjvsemproj.models.maps;

// TODO rename to MapConfig and make it record
// TODO think if we need to add Tile class
public class GameMap {
    private final Tile[][] tiles;

    public GameMap(int xLength, int yLength) {
        this.tiles = new Tile[xLength][yLength];

        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                this.tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}
