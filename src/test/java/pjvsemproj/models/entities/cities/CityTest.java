package pjvsemproj.models.entities.cities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.BotPlayer;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.GameSetupManager;
import pjvsemproj.models.maps.GameMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityTest {

    private static Game game;
    private static Player player1;
    private static Player player2;

    @BeforeAll
    public static void init() {
        GameSetupManager gsm = new GameSetupManager();
        player1 = new HumanPlayer("Vasya", 0);
        player2 = new BotPlayer("Ivan", 0);
        game = gsm.setupStandardMatch(
                new GameMap(25, 25),
                player1,
                player2
        );
    }

    @Test
    public void getOwner_returnsPlayerClassInsteadOfIOwnerInterface() {
        City p1city = player1.getCities().getFirst();
        Player player = p1city.getOwner();

        assertEquals(HumanPlayer.class, player.getClass());
    }
}
