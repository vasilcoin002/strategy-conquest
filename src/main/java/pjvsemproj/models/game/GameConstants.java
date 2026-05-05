package pjvsemproj.models.game;

public final class GameConstants {
    private GameConstants() {}

    public static final int MIN_MAP_WIDTH = 5;
    public static final int MIN_MAP_HEIGHT = 5;
    public static final int PLAYERS_COUNT = 2;
    public static final String[] ENTITY_TYPES = {
            "City", // city
            "Militia", "Infantry", "Cavalry", "Artillery" // troops
    };
}
