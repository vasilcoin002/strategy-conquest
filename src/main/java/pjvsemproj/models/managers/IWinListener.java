package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;


/**
 * Listener interface for win events.
 */
public interface IWinListener {
    void onWin(Player winner);
}
