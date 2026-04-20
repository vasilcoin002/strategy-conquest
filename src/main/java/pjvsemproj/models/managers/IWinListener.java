package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;


/**
 * Listener interface for win events initialized by ConquestManager.
 */
public interface IWinListener {
    void onWin(Player winner);
}
