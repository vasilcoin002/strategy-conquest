package pjvsemproj.models.entities;

/**
 * Represents an object that can be bought for gold.
 */
public interface Purchasable {

    /**
     * Retrieves the economic cost required to purchase or spawn this entity.
     *
     * @return the price in gold
     */
    int getPrice();
}