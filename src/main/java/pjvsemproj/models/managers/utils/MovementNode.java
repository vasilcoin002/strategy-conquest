package pjvsemproj.models.managers.utils;

import pjvsemproj.models.game.maps.Tile;

/**
 * Node used in MovementManager for pathfinding algorithm
 *
 * @param tile     the map tile represented by this node
 * @param distance the accumulated movement cost or distance required to reach this tile from the origin
 */
public record MovementNode(Tile tile, int distance) {}
