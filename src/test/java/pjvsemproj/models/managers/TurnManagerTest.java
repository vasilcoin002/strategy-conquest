package pjvsemproj.models.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnManagerTest {

    private Player player1;
    private Player player2;
    private TurnManager turnManager;

    @BeforeEach
    void setUp() {
        player1 = new HumanPlayer("Player 1", 100);
        player2 = new HumanPlayer("Player 2", 100);
        turnManager = new TurnManager(player1, player2, player1);
    }

    @Test
    void nextTurn_CyclesToNextPlayer() {
        assertEquals(player1, turnManager.getCurrentPlayer(), "Turn 1 must belong to Player 1");

        turnManager.endTurn();

        assertEquals(player2, turnManager.getCurrentPlayer(), "Turn 2 must belong to Player 2");
    }

    @Test
    void nextTurn_WrapsAroundToFirstPlayer() {
        turnManager.endTurn(); // P1 -> P2
        turnManager.endTurn(); // P2 -> P1

        assertEquals(player1, turnManager.getCurrentPlayer(), "Turn 3 must wrap back to Player 1");
    }

    @Test
    void nextTurn_NotifiesTurnListenersWithNewActivePlayer() {
        class TestTurnListener implements ITurnListener {
            Player notifiedPlayer = null;
            int triggerCount = 0;

            @Override
            public void onTurnStart(Player activePlayer) {
                this.notifiedPlayer = activePlayer;
                this.triggerCount++;
            }

            @Override
            public void onTurnEnd(Player endingPlayer) {

            }
        }

        TestTurnListener listener = new TestTurnListener();
        turnManager.addTurnListener(listener);

        turnManager.endTurn();

        assertEquals(1, listener.triggerCount, "Listener must be triggered exactly once per turn change");
        assertEquals(player2, listener.notifiedPlayer, "Listener must be notified with the new active player's instance");
    }
}