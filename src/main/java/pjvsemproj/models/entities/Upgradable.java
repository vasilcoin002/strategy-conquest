package pjvsemproj.models.entities;


/**
 * Represents an object that can be upgraded to higher levels.
 *
 * @param <T> type describing the current upgrade level
 */
public interface Upgradable<T> {
    boolean canBeUpgraded();
    T getCurrentLevel();
}
