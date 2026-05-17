package pjvsemproj.models.services;

import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.BotExecutor;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Local (single-player) implementation of GameService.
 * <p>
 * Directly interacts with managers to execute game actions and handles the automated
 * execution of AI opponents when it is their turn.
 */
public class LocalGameService extends AbstractClientService {

    /**
     * Constructs a local single-player game service.
     *
     * @param game the initial game state
     */
    public LocalGameService(Game game) {
        super(game);
    }

    /**
     * Signals that the client is ready. Currently unimplemented for local games
     * as no network synchronization is required.
     */
    @Override
    public void ready() {

    }

    /**
     * Ends the current turn and checks if the next player is controlled by the AI.
     * If so, it asynchronously triggers the bot's turn to prevent freezing the UI.
     */
    @Override
    public void endTurn() {
        super.endTurn();

        if (turnManager.getCurrentPlayer() instanceof BotPlayer) {
            // using CompletableFuture instead of new Thread() to efficiently reuse threads
            // from Java's built-in pool, saving memory and avoiding heavy OS thread creation.
            CompletableFuture.runAsync(() -> {
                playBotTurn();

                endTurn();
            });
        }
    }

    /**
     * Simulates the bot "thinking" and executes its calculated turn actions.
     */
    private void playBotTurn() {
        System.out.println("Bot is making its moves...");

        // simulation of thinking
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO log
        }

        BotExecutor bot = new BotExecutor(this);
        bot.playTurnActionsOnly();
    }

    /**
     * Bypasses standard fog-of-war restrictions so the local bot can evaluate its attack options.
     *
     * @param unitId the bot's unit
     * @return a set of attackable tiles
     */
    public Set<TileDTO> getBotAttackTiles(String unitId) {
        return getUnrestrictedAttackTiles(unitId);
    }

    /**
     * Bypasses standard fog-of-war restrictions so the local bot can evaluate its movement options.
     *
     * @param unitId the bot's unit
     * @return a set of reachable tiles
     */
    public Set<TileDTO> getBotMoveTiles(String unitId) {
        return getUnrestrictedMovementTiles(unitId);
    }

    /**
     * Determines if the UI should allow input based on whether the active player is human.
     *
     * @return {@code true} if a human player is currently active
     */
    @Override
    public boolean isMyTurn() {
        return turnManager.getCurrentPlayer() instanceof HumanPlayer;
    }
}