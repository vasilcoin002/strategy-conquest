package pjvsemproj.models.managers;

import pjvsemproj.models.game.players.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Handles turn switching between players and
 * notifies all registered listeners about turn events.
 */
public class TurnManager {
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private int turnNumber;

    private final List<ITurnListener> listeners = new ArrayList<>();

    /**
     * Constructs the TurnManager to regulate the game cycle between two players.
     *
     * @param p1            the first player
     * @param p2            the second player
     * @param currentPlayer the player designated to take the first turn (defaults to p1 if null)
     */
    public TurnManager(Player p1, Player p2, Player currentPlayer) {
        this.player1 = p1;
        this.player2 = p2;
        this.turnNumber = 1;
        if (currentPlayer == null) {
            this.currentPlayer = p1;
        } else {
            this.currentPlayer = currentPlayer;
        }
    }

    /**
     * Registers a turn listener to receive callbacks when turns start and end.
     *
     * @param listener the observer implementing the ITurnListener interface
     */
    public void addTurnListener(ITurnListener listener) {
        listeners.add(listener);
    }

    /**
     * Concludes the current player's turn, advances the global turn counter if necessary,
     * switches the active player, and triggers the start of the next turn.
     */
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

    /**
     * Explicitly triggers the start of a turn for the specified player,
     * broadcasting the event to all registered listeners.
     *
     * @param player the player whose turn is beginning
     */
    public void startTurn(Player player) {
        for (ITurnListener listener : listeners) {
            listener.onTurnStart(player);
        }
    }

    /**
     * Retrieves the first player.
     *
     * @return player 1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Retrieves the second player.
     *
     * @return player 2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Retrieves the player whose turn is currently active.
     *
     * @return the current active player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Retrieves the current global turn cycle number.
     *
     * @return the current turn number
     */
    public int getTurnNumber() {
        return turnNumber;
    }
}