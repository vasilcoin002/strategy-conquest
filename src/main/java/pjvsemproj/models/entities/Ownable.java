package pjvsemproj.models.entities;

import pjvsemproj.models.game.players.Player;

/**
 * Represents an object that can belong to a player.
 */
public interface Ownable {

    /**
     * Retrieves the player who currently controls or owns this entity.
     *
     * @return the owning player, or {@code null} if neutral/unowned
     */
    Player getOwner();

    /**
     * Assigns control or ownership of this entity to a specific player.
     *
     * @param owner the new owning player
     */
    void setOwner(Player owner);
}