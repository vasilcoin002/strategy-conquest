package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;

import java.util.concurrent.CompletableFuture;

// TODO fix setting up with bot's first turn blocks next turn button disabled for the rest of the game
/**
 * Local (single-player) implementation of GameService.
 * <p>
 * Directly interacts with managers to execute game actions.
 */
public class LocalGameService extends AbstractClientService {

    public LocalGameService(Game game) {
        super(game);
    }

    @Override
    public void ready() {

    }

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

    private void playBotTurn() {
        System.out.println("Bot is making its moves...");

        // simulation of thinking
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO extend playBotTurn with bot's turn logic
    }

    @Override
    public boolean isMyTurn() {
        return turnManager.getCurrentPlayer() instanceof HumanPlayer;
    }
}
