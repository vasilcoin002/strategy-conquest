package pjvsemproj.models.entities;

public interface IDamager {
    int calculateDamage();
    int getAttackRange();
    boolean hasAttackedThisTurn();
    void setHasAttackedThisTurn(boolean attacked);
}
