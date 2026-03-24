package pjvsemproj.models.maps;

// TODO rename to MapConfig and make it record
// TODO think if we need to add Tile class
public class GameMap {
    public final int boundWest;
    public final int boundEast;
    public final int boundNorth;
    public final int boundSouth;

    public GameMap(int boundWest, int boundEast, int boundNorth, int boundSouth) {
        this.boundWest = boundWest;
        this.boundEast = boundEast;
        this.boundNorth = boundNorth;
        this.boundSouth = boundSouth;
    }
}
