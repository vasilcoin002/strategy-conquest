package pjvsemproj.models.entities;

import pjvsemproj.models.game.maps.Tile;

/**
 * Abstract entity implementation that supports health, damage and healing.
 * <p>
 * Extends {@link Entity} and provides reusable logic for clamping health
 * between zero and maximum value.
 */
public abstract class DamageableEntity extends Entity implements Damageable {

    protected int health;
    protected int maxHealth;

    /**
     * Constructs a DamageableEntity with a generated UUID.
     *
     * @param initialTile the starting tile where the entity is placed
     * @param isPassable  {@code true} if other entities can move through this entity's tile
     */
    public DamageableEntity(Tile initialTile, boolean isPassable) {
        super(initialTile, isPassable);
    }

    /**
     * Constructs a DamageableEntity with a specific ID.
     *
     * @param id          the specific unique identifier for this entity
     * @param initialTile the starting tile where the entity is placed
     * @param isPassable  {@code true} if other entities can move through this entity's tile
     */
    public DamageableEntity(String id, Tile initialTile, boolean isPassable) {
        super(id, initialTile, isPassable);
    }

    /**
     * Applies damage to this entity.
     * Health cannot go below zero.
     *
     * @param damage amount of damage to apply
     */
    @Override
    public void takeDamage(int damage) {
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Applies healing to this entity.
     * Health cannot exceed maximum health.
     *
     * @param heal amount of healing to apply
     */
    @Override
    public void takeHeal(int heal) {
        this.health += heal;

        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    /**
     * Returns current health value.
     *
     * @return current health
     */
    @Override
    public int getHealth() {
        return this.health;
    }

    /**
     * Sets current health value.
     * The value is capped at maximum health.
     *
     * @param health new health value
     */
    @Override
    public void setHealth(int health) {
        this.health = health;

        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    /**
     * Returns maximum health.
     *
     * @return maximum health
     */
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Checks whether this entity has no remaining health.
     *
     * @return {@code true} if health is 0 or less
     */
    @Override
    public boolean isDead() {
        return this.health <= 0;
    }
}