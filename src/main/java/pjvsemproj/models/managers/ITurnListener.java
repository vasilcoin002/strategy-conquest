package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

/**
 * Listener interface for turn events initialized by TurnManager.
 */
public interface ITurnListener {

    /**
     * Invoked immediately when a new player's turn begins.
     *
     * @param activePlayer the player whose turn has just started
     */
    void onTurnStart(Player activePlayer);

    /**
     * Invoked immediately when a player's turn ends, before the next player takes control.
     *
     * @param endingPlayer the player whose turn has just concluded
     */
    void onTurnEnd(Player endingPlayer);
}