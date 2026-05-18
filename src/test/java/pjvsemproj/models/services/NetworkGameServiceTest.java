package pjvsemproj.models.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.server.Client;

import static org.mockito.Mockito.*;

class NetworkGameServiceTest {

    private Client client;
    private Game game;
    private NetworkGameService service;

    @BeforeEach
    void setUp() {
        client = mock(Client.class);

        GameMap map = new GameMap(10, 10);
        game = new Game(map);

        HumanPlayer p1 = new HumanPlayer("Player1", 100);
        HumanPlayer p2 = new HumanPlayer("Player2", 100);

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.setCurrentPlayer(p1);

        service = new NetworkGameService(client, game);
    }

    @Test
    void moveUnit_ShouldSendMoveCommandToClient() {
        service.moveUnit("unit-1", 3, 4);

        verify(client).moveUnit("unit-1", 3, 4);
    }

    @Test
    void attack_ShouldSendAttackCommandToClient() {
        service.attack("attacker-1", "target-1");

        verify(client).attack("attacker-1", "target-1");
    }

    @Test
    void endTurn_ShouldSendEndTurnCommandToClient() {
        service.endTurn();

        verify(client).endTurn();
    }
}