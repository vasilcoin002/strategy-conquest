package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

public interface ITurnListener {
    void onTurnStart(Player activePlayer);
    void onTurnEnd(Player endingPlayer);
}
