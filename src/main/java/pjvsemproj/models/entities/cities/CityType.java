package pjvsemproj.models.entities.cities;

/**
 * Defines available city levels, their economic properties, and upgrade paths.
 * <p>
 * Note: The enums are declared in reverse order (LEVEL_3 to LEVEL_1) to safely
 * allow lower levels to reference higher levels during JVM initialization.
 */
public enum CityType {

    /** Maximum city level. Generates high gold and cannot be upgraded further. */
    LEVEL_3(40, 0, null),

    /** Intermediate city level. Generates moderate gold and upgrades to LEVEL_3. */
    LEVEL_2(30, 70, CityType.LEVEL_3),

    /** Base city level. Generates low gold and upgrades to LEVEL_2. */
    LEVEL_1(15, 40, CityType.LEVEL_2);

    /** The amount of gold this city generates at the end of every economy turn. */
    public final int goldProducedPerRound;
    /** The cost in gold required to upgrade to the {@code nextCityType}. */
    public final int upgradePrice;
    /** The subsequent city level this city becomes upon upgrading (null if max level). */
    public final CityType nextCityType;

    /**
     * Constructs a CityType configuration.
     *
     * @param goldProducedPerRound the economic output per turn
     * @param upgradePrice         the cost to reach the next tier
     * @param nextCityType         the target level for upgrades, or null if this is the maximum tier
     */
    CityType(int goldProducedPerRound, int upgradePrice, CityType nextCityType) {
        this.goldProducedPerRound = goldProducedPerRound;
        this.upgradePrice = upgradePrice;
        this.nextCityType = nextCityType;
    }
}