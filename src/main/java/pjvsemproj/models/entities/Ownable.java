package pjvsemproj.models.entities;

import pjvsemproj.models.game.players.Player;

/**
 * Represents an object that can belong to a player.
 */
public interface Ownable {
    Player getOwner();
    void setOwner(Player owner);
}