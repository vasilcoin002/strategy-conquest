package pjvsemproj.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.models.services.CoreGameService;

import java.util.List;

import static org.mockito.Mockito.*;

public class GameSessionMockitoTest {

    private GameServer server;
    private Connection connection1;
    private Connection connection2;
    private CoreGameService service;
    private GameSession session;

    @BeforeEach
    void setUp() {
        server = mock(GameServer.class);
        connection1 = mock(Connection.class);
        connection2 = mock(Connection.class);
        service = mock(CoreGameService.class);

        when(connection1.getPlayerName()).thenReturn("Player1");
        when(connection2.getPlayerName()).thenReturn("Player2");

        when(service.getPlayersDTO()).thenReturn(List.of(
                new PlayerDTO("Player1", 100),
                new PlayerDTO("Player2", 100)
        ));

        when(service.getGameDTO()).thenReturn(
                new GameDTO(5, 5, List.of(), List.of(
                        new PlayerDTO("Player1", 100),
                        new PlayerDTO("Player2", 100)
                ), "Player1")
        );

        when(service.getCurrentPlayerDTO()).thenReturn(
                new PlayerDTO("Player1", 100)
        );

        session = new GameSession(server, connection1, connection2, service);
        session.startGame();
    }

    @Test
    void onMove_WhenMoveIsValid_ShouldBroadcastUnitMoved() {
        when(service.moveUnit("unit-1", 2, 3)).thenReturn(true);

        session.onMove(connection1, "unit-1", 2, 3);

        verify(service).moveUnit("unit-1", 2, 3);
        verify(connection1).sendToClient(Protocol.UNIT_MOVED, "unit-1", "2", "3");
        verify(connection2).sendToClient(Protocol.UNIT_MOVED, "unit-1", "2", "3");
    }

    @Test
    void onMove_WhenNotPlayersTurn_ShouldSendError() {
        session.onMove(connection2, "unit-1", 2, 3);

        verify(connection2).sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
        verify(service, never()).moveUnit(anyString(), anyInt(), anyInt());
    }

    @Test
    void onMove_WhenMoveFails_ShouldSendMoveFailedError() {
        when(service.moveUnit("unit-1", 2, 3)).thenReturn(false);

        session.onMove(connection1, "unit-1", 2, 3);

        verify(service).moveUnit("unit-1", 2, 3);
        verify(connection1).sendToClient(Protocol.ERROR, "MOVE_FAILED");
        verify(connection1, never()).sendToClient(Protocol.UNIT_MOVED, "unit-1", "2", "3");
        verify(connection2, never()).sendToClient(Protocol.UNIT_MOVED, "unit-1", "2", "3");
    }

    @Test
    void onCityUpgrade_WhenUpgradeSucceeds_ShouldBroadcastCityUpgraded() {
        when(service.upgradeCity("city-1")).thenReturn(true);

        session.onCityUpgrade(connection1, "city-1");

        verify(service).upgradeCity("city-1");
        verify(connection1).sendToClient(Protocol.CITY_UPGRADED, "city-1");
        verify(connection2).sendToClient(Protocol.CITY_UPGRADED, "city-1");
    }

    @Test
    void onEndTurn_WhenCurrentPlayerEndsTurn_ShouldBroadcastTurnStarted() {
        when(service.getCurrentPlayerDTO())
                .thenReturn(new PlayerDTO("Player1", 100))
                .thenReturn(new PlayerDTO("Player2", 100));

        session.onEndTurn(connection1);

        verify(service).endTurn();
        verify(connection1).sendToClient(Protocol.TURN_STARTED, "Player2");
        verify(connection2).sendToClient(Protocol.TURN_STARTED, "Player2");
    }

    @Test
    void onPlayerQuit_ShouldDeclareRemainingPlayerWinner_AndStopServer() {
        session.onPlayerQuit(connection1);

        verify(connection2).sendToClient(Protocol.GAME_OVER, "Player2");
        verify(server).removeSession(session);
        verify(server).stopServer();
    }
}