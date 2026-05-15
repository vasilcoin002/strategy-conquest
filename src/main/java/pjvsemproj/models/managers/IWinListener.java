package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

/**
 * Listener functional interface for win events initialized by ConquestManager.
 */
public interface IWinListener {

    /**
     * Invoked when a player successfully completes the victory conditions of the game.
     *
     * @param winner the player who won the game
     */
    void onWin(Player winner);
}