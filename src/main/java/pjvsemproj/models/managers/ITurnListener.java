package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

/**
 * Interface for classes that react to turn changes.
 */

public interface ITurnListener {
    void onTurnStart(Player activePlayer);
    void onTurnEnd(Player endingPlayer);
}
