package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

/**
 * Listener interface for turn events initialized by TurnManager.
 */
public interface ITurnListener {
    void onTurnStart(Player activePlayer);
    void onTurnEnd(Player endingPlayer);
}
