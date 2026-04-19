package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Controls turn switching between players.
 *
 * Notifies all registered listeners about turn events.
 */
public class TurnManager {
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private int turnNumber;

    private final List<ITurnListener> listeners = new ArrayList<>();

    public TurnManager(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.turnNumber = 1;
        this.currentPlayer = p1;
    }
    /**
     * Registers a turn listener.
     */
    public void addTurnListener(ITurnListener listener) {
        listeners.add(listener);
    }

    public void endTurn() {
        for (ITurnListener listener : listeners) {
            listener.onTurnEnd(currentPlayer);
        }

        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
            turnNumber++;
        }

        startTurn(currentPlayer);
    }

    private void startTurn(Player player) {
        for (ITurnListener listener : listeners) {
            listener.onTurnStart(player);
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}