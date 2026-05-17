package pjvsemproj.config;

import pjvsemproj.models.entities.cities.CityType;
import pjvsemproj.models.entities.troopUnits.TroopType;

/**
 * A centralized utility defining strict fallback parameters for incomplete game configurations.
 * <p>
 * These immutable constants are utilized primarily by the {@link GameConfigSanitizer} to patch
 * missing data from external sources (such as JSON save files) before the validation phase occurs.
 * Instantiation is disabled to enforce a pure constants-holding structure.
 */
public final class ConfigDefaultValues {

    private ConfigDefaultValues() {
        // Prevent instantiation of utility class
    }

    // map
    public static final int DEFAULT_MAP_WIDTH = 10;
    public static final int DEFAULT_MAP_HEIGHT = 10;

    // players
    public static final int DEFAULT_INIT_BALANCE = 0;

    // troops
    public static final boolean DEFAULT_HAS_MOVED_THIS_TURN = false;
    public static final boolean DEFAULT_HAS_ATTACKED_THIS_TURN = false;

    /**
     * Resolves the default health capacity for a given military unit based on its classification.
     * @param troopType The classification enum of the unit.
     * @return The absolute maximum health integer associated with the type.
     */
    public static int getDefaultHp(TroopType troopType) {
        return troopType.maxHealth;
    }

    // cities
    public static final String DEFAULT_CITY_LEVEL = CityType.LEVEL_1.name();
}