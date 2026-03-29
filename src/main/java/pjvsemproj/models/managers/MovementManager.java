package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

public class MovementManager implements ITurnListener {

    private Player currentPlayer;

    @Override
    public void onTurnStart(Player activePlayer) {
        currentPlayer = activePlayer;
        activePlayer.getTroops().forEach(
                troop -> troop.setHasMovedThisTurn(false)
        );
    }

    @Override
    public void onTurnEnd(Player endingPlayer) {}
}
