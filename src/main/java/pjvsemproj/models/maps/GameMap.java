package pjvsemproj.models.maps;

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
