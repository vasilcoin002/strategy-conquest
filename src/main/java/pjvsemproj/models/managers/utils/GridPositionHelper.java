package pjvsemproj.models.managers.utils;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

/**
 * Utility class for synchronizing the bidirectional relationship between {@link IGridEntity} objects and map {@link Tile}s.
 * <p>
 * Ensures that when an entity moves or is placed, both the entity's internal tile reference
 * and the tile's internal list of entities are updated atomically to prevent game state desynchronization.
 */
public class GridPositionHelper {

    /**
     * Safely places an entity on a specific tile and synchronizes their relationship.
     * Typically used during initial game setup or when spawning new units.
     *
     * @param entity     the entity to place
     * @param targetTile the tile where the entity should be placed
     * @return {@code true} if the entity was successfully placed, {@code false} if the tile was null or blocked
     */
    public static boolean placeEntity(IGridEntity entity, Tile targetTile) {
        if (targetTile == null) return false;

        if (targetTile.addEntity(entity)) {
            entity.setTile(targetTile);
            return true;
        }
        return false;
    }

    /**
     * Completely removes an entity from the game board.
     * Severs the bidirectional link between the entity and its current tile.
     * Typically used when an entity is destroyed in combat.
     *
     * @param entity the entity to remove
     */
    public static void removeFromBoard(IGridEntity entity) {
        Tile currentTile = entity.getTile();
        if (currentTile != null) {
            currentTile.removeEntity(entity);
            entity.setTile(null);
        }
    }

    /**
     * Safely teleports an entity from its current tile to a new target tile.
     * Handles removing the entity from the old tile and adding it to the new one atomically.
     * Typically used during the movement phase of a turn.
     *
     * @param entity     the entity to move
     * @param targetTile the destination tile
     * @return {@code true} if the movement was successful, {@code false} if the target is null or blocked
     */
    public static boolean moveEntity(IGridEntity entity, Tile targetTile) {
        if (targetTile == null || targetTile.isBlocked()) return false;

        Tile oldTile = entity.getTile();
        if (targetTile.addEntity(entity)) {
            if (oldTile != null) {
                oldTile.removeEntity(entity);
            }
            entity.setTile(targetTile);
            return true;
        }
        return false;
    }
}