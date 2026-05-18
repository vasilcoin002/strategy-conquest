package pjvsemproj.models.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;

import static org.junit.jupiter.api.Assertions.*;

class LocalGameServiceTest {

    private Game game;
    private LocalGameService service;
    private BotPlayer botPlayer;
    private HumanPlayer humanPlayer;

    @BeforeEach
    void setUp() {
        GameMap map = new GameMap(10, 10);
        game = new Game(map);

        botPlayer = new BotPlayer("Bot", 100);
        humanPlayer = new HumanPlayer("Human", 100);

        game.addPlayer(botPlayer);
        game.addPlayer(humanPlayer);

        service = new LocalGameService(game);
    }

    @Test
    void ready_WhenBotIsCurrentPlayer_TriggersBotTurnAndPassesToHuman() throws InterruptedException {
        game.setCurrentPlayer(botPlayer);
        assertEquals(botPlayer, game.getCurrentPlayer(), "Bot should be the initial current player.");

        service.ready();

        Thread.sleep(2500);

        assertEquals(humanPlayer, game.getCurrentPlayer(), "The turn should have advanced to the Human player.");
    }

    @Test
    void ready_WhenHumanIsCurrentPlayer_DoesNothing() throws InterruptedException {
        game.setCurrentPlayer(humanPlayer);
        assertEquals(humanPlayer, game.getCurrentPlayer(), "Human should be the initial current player.");

        service.ready();

        Thread.sleep(500);

        assertEquals(humanPlayer, game.getCurrentPlayer(), "The turn should not advance if the current player is human.");
    }
}