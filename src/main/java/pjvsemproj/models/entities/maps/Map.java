package pjvsemproj.models.entities.maps;

public class Map {
    public final int boundWest;
    public final int boundEast;
    public final int boundNorth;
    public final int boundSouth;

    public Map(int boundWest, int boundEast, int boundNorth, int boundSouth) {
        this.boundWest = boundWest;
        this.boundEast = boundEast;
        this.boundNorth = boundNorth;
        this.boundSouth = boundSouth;
    }
}
