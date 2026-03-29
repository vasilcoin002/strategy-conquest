package pjvsemproj.models.entities;

import pjvsemproj.models.game.players.Player;

public interface Ownable {
    Player getOwner();
    void setOwner(Player owner);
}