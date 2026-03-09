package pjvsemproj.models.entities;

public interface IDamager<T extends Damageable> {
    void attack(T damageTaker);
}
