package pjvsemproj.models.entities;

public interface Upgradable<T> {
    boolean canBeUpgraded();
    T getCurrentLevel();
}
