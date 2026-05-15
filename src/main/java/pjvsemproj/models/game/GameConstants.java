package pjvsemproj.models.game;

import java.util.List;

/**
 * Contains global constants used throughout the game's configuration, validation, and domain logic.
 * <p>
 * This class cannot be instantiated.
 */
public final class GameConstants {

    private GameConstants() {}

    /** The minimum allowed width for a generated or loaded game map. */
    public static final int MIN_MAP_WIDTH = 5;

    /** The minimum allowed height for a generated or loaded game map. */
    public static final int MIN_MAP_HEIGHT = 5;

    /** The strict number of players required for a match to successfully start. */
    public static final int PLAYERS_COUNT = 2;

    /** * The valid string identifiers for all entity types that can be parsed from JSON configurations.
     * <p>
     * Note: In Java, array contents are mutable. Do not overwrite the indexes of this array.
     */
    public static final List<String> ENTITY_TYPES = List.of(
            "City", // city
            "Militia", "Infantry", "Cavalry", "Artillery" // troops
    );

    /** * The percentage of a unit's maximum HP that is recovered when healing inside a city.
     * E.g., 0.25 represents healing 25% of max HP per turn.
     */
    public static final double TROOP_HEALING_PERCENT_BY_MAX_HP = 0.25;
}