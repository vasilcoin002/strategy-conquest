package pjvsemproj.models.managers.utils;

import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

/**
 * Helper class for syncing bidirectional relationship between Entities and Tiles
 */
public class GridPositionHelper {

    /**
     * Safely places an entity on a tile and syncs the relationship.
     * Usage: spawning and setup entities
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
     * Safely removes an entity from the board completely.
     * Usage: removing damageable entities when they're dead
     */
    public static void removeFromBoard(IGridEntity entity) {
        Tile currentTile = entity.getTile();
        if (currentTile != null) {
            currentTile.removeEntity(entity);
            entity.setTile(null);
        }
    }

    /**
     * Safely teleports an entity from its current tile to a new one.
     * Usage: moving movable entities!
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