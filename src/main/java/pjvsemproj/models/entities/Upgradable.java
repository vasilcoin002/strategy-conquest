package pjvsemproj.models.entities;

/**
 * Represents an object that can be upgraded to higher levels.
 *
 * @param <T> type describing the current upgrade level
 */
public interface Upgradable<T> {

    /**
     * Evaluates whether this entity is eligible to be upgraded to a higher tier.
     *
     * @return {@code true} if a higher upgrade level exists and is valid
     */
    boolean canBeUpgraded();

    /**
     * Retrieves the current tier or upgrade level of the entity.
     *
     * @return the current level state
     */
    T getCurrentLevel();
}