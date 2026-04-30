package pjvsemproj.dto;

import pjvsemproj.models.entities.cities.City;

public class CityDTO extends EntityDTO {
    public final String cityLevel;
    public final int upgradePrice;
    public final boolean canBeUpgraded;
    public final int goldProducedPerRound;

    // Attribute shows if tile on which stands city is currently blocked or not
    public final boolean canSpawnTroops;

    public CityDTO(
            String id, String entityType, int x, int y, String ownerName,
            String cityLevel, int upgradePrice,
            boolean canBeUpgraded, int goldProducedPerRound,
            boolean canSpawnTroops
    ) {
        super(id, entityType, x, y, ownerName);
        this.cityLevel = cityLevel;
        this.upgradePrice = upgradePrice;
        this.canBeUpgraded = canBeUpgraded;
        this.goldProducedPerRound = goldProducedPerRound;
        this.canSpawnTroops = canSpawnTroops;
    }

    public CityDTO(City city) {
        super(
                city.getId(),
                "City",
                city.getTile().getX(),
                city.getTile().getY(),
                city.getOwner() != null ? city.getOwner().getName() : "Neutral"
        );

        this.cityLevel = city.getCityType().name();
        this.upgradePrice = city.getUpgradePrice();
        this.canBeUpgraded = city.canBeUpgraded();
        this.goldProducedPerRound = city.getGoldProducedPerRound();
        this.canSpawnTroops = !city.getTile().isBlocked();
    }
}
