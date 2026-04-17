package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

public interface IWinListener {
    void onWin(Player winner);
}
