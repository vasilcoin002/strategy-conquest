package pjvsemproj.models.game;

import pjvsemproj.models.game.players.Player;

public interface ITurnListener {
    void onTurnStart(Player activePlayer);
    void onTurnEnd(Player endingPlayer);
}
