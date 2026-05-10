package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;


/**
 * Listener functional interface for win events initialized by ConquestManager.
 */
public interface IWinListener {
    void onWin(Player winner);
}
