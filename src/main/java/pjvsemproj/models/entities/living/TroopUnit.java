package pjvsemproj.models.entities.living;

import pjvsemproj.models.entities.Damageable;
import pjvsemproj.models.entities.Entity;
import pjvsemproj.models.entities.IDamager;
import pjvsemproj.models.entities.ILiving;

public abstract class TroopUnit extends Entity implements ILiving, IDamager<Damageable> {

    protected int minDamage;
    protected int maxDamage;
    protected int speed;

    public TroopUnit() {
        super(false);
    }

    @Override
    public void takeDamage(int damage) {

    }

    @Override
    public void takeHeal(int heal) {

    }

    @Override
    public void setHealth(int health) {

    }

    @Override
    public void attack(Damageable damageTaker) {

    }

    @Override
    public void moveUp() {

    }

    @Override
    public void moveDown() {

    }

    @Override
    public void moveLeft() {

    }

    @Override
    public void moveRight() {

    }

    @Override
    public void moveUp(int step) {

    }

    @Override
    public void moveDown(int step) {

    }

    @Override
    public void moveLeft(int step) {

    }

    @Override
    public void moveRight(int step) {

    }
}
