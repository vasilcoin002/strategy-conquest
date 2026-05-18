package pjvsemproj.server;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.services.NetworkGameService;

import static org.mockito.Mockito.*;

public class NetworkGameListenerMockitoTest {

    private NetworkGameService service;
    private NetworkGameListener listener;

    @BeforeEach
    void setUp() {
        service = mock(NetworkGameService.class);
        listener = new NetworkGameListener(service);
    }

    @Test
    void onUnitMoved_ShouldApplyServerMove() {
        listener.onUnitMoved("unit-1", 5, 6);

        verify(service).applyServerMove("unit-1", 5, 6);
    }

    @Test
    void onUnitAttacked_ShouldApplyServerAttack() {
        listener.onUnitAttacked("attacker-1", "target-1", 25);

        verify(service).applyServerAttack("attacker-1", "target-1", 25);
    }

    @Test
    void onTurnStarted_ShouldApplyServerTurnStarted() {
        listener.onTurnStarted("Player2");

        verify(service).applyServerTurnStarted();
    }

    @Test
    void onGameOver_ShouldApplyServerGameOver() {
        listener.onGameOver("Player1");

        verify(service).applyServerGameOver("Player1");
    }
}
