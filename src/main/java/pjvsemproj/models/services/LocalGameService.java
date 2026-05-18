package pjvsemproj.models.services;

import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.BotExecutor;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Local (single-player) implementation of GameService.
 * <p>
 * Directly interacts with managers to execute game actions and handles the automated
 * execution of AI opponents when it is their turn.
 */
public class LocalGameService extends AbstractClientService {

    /**
     * Constructs a local single-player game service engine.
     *
     * @param game The initial domain {@link Game} simulation state model to manage.
     */
    public LocalGameService(Game game) {
        super(game);
    }

    /**
     * Signals that the client is ready and the game view has been loaded.
     * <p>
     * If a game is loaded from a save file and it is currently the AI's turn,
     * this method kickstarts the bot's execution loop so the game doesn't softlock.
     */
    @Override
    public void ready() {
        if (turnManager.getCurrentPlayer() instanceof BotPlayer) {
            CompletableFuture.runAsync(() -> {
                playBotTurn();
                endTurn();
            });
        }
    }

    /**
     * Ends the current turn and checks if the subsequent player is controlled by an AI script.
     * <p>
     * If the upcoming participant is verified as a {@link BotPlayer}, it asynchronously
     * triggers the automated thinking routines via {@link CompletableFuture#runAsync(Runnable)}
     * to prevent blocking or freezing the main UI application thread.
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
     * Simulates the AI bot "thinking" parameters and dispatches its calculated turn actions.
     * <p>
     * Induces a structural thread sleep interval to mimic cognitive delays, then delegates choice patterns
     * over to a newly instantiated {@link BotExecutor} module.
     */
    private void playBotTurn() {
        Logger.getLogger(LocalGameService.class.getName()).info("Bot is making its moves...");

        // simulation of thinking
        try {
            Thread.sleep(2000);
        } catch (InterruptedException _) {

        }

        BotExecutor bot = new BotExecutor(this);
        bot.playTurnActionsOnly();
    }

    /**
     * Bypasses standard visibility or fog-of-war constraints so the local bot can evaluate its complete combat attack options.
     *
     * @param unitId Unique lookup token identifier string matching the bot's tracking military unit.
     * @return A {@link Set} containing unconstrained target cell {@link TileDTO} objects that the bot unit can legally hit.
     */
    public Set<TileDTO> getBotAttackTiles(String unitId) {
        return getUnrestrictedAttackTiles(unitId);
    }

    /**
     * Bypasses standard visibility or fog-of-war constraints so the local bot can evaluate its complete spatial movement options.
     *
     * @param unitId Unique lookup token identifier string matching the bot's tracking military unit.
     * @return A {@link Set} containing unconstrained reachable path cell {@link TileDTO} objects that the bot unit can legally traverse.
     */
    public Set<TileDTO> getBotMoveTiles(String unitId) {
        return getUnrestrictedMovementTiles(unitId);
    }

    /**
     * Determines if the graphical user interface should unlock component inputs based on active player types.
     *
     * @return {@code true} if a human player is currently active and holding interaction clearances;
     * {@code false} if an AI bot is processing moves.
     */
    @Override
    public boolean isMyTurn() {
        return turnManager.getCurrentPlayer() instanceof HumanPlayer;
    }
}