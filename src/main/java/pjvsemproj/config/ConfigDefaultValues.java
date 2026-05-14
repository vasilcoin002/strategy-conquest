package pjvsemproj.config;

import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;

public final class ConfigDefaultValues {
    private ConfigDefaultValues() {}

    // map
    public static final int DEFAULT_MAP_WIDTH = 10;
    public static final int DEFAULT_MAP_HEIGHT = 10;

    // players
    public static final int DEFAULT_INIT_BALANCE = 0;
    public static final int DEFAULT_CURRENT_PLAYER_NUM = 1;

    // troops
    public static final boolean DEFAULT_HAS_MOVED_THIS_TURN = false;
    public static final boolean DEFAULT_HAS_ATTACKED_THIS_TURN = false;
    public static int getDefaultHp(TroopType troopType) {
        return troopType.maxHealth;
    };

    // cities
    public static final String DEFAULT_CITY_LEVEL = CityType.LEVEL_1.name();
}
