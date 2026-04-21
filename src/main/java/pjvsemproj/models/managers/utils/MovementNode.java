package pjvsemproj.models.managers.utils;

import pjvsemproj.models.game.maps.Tile;

/**
 * Node used in MovementManager for pathfinding algorithm
 *
 * @param tile
 * @param distance
 */
public record MovementNode(Tile tile, int distance) {}
