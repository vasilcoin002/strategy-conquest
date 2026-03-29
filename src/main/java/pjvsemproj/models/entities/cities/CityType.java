package pjvsemproj.models.entities.cities;

public enum CityType {
    LEVEL_3(40, 0, null),
    LEVEL_2(30, 70, CityType.LEVEL_3),
    LEVEL_1(15, 40, CityType.LEVEL_2);

    public final int goldProducedPerRound, upgradePrice;
    public final CityType nextCityType;

    CityType(int goldProducedPerRound, int upgradePrice, CityType nextCityType) {
        this.goldProducedPerRound = goldProducedPerRound;
        this.upgradePrice = upgradePrice;
        this.nextCityType = nextCityType;
    }
}
