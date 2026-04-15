package pjvsemproj.models.entities;

/**
 * Represents an entity capable of dealing combat damage.
 */

public interface IDamager {
    int calculateDamage();
    int getAttackRange();
    boolean hasAttackedThisTurn();
    void setHasAttackedThisTurn(boolean attacked);

    int getMinDamage();
    int getMaxDamage();
}
