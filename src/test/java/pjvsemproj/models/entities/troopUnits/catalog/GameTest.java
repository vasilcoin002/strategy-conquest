//package pjvsemproj.models.entities.troopUnits.catalog;
//
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import pjvsemproj.models.entities.cities.City;
//import pjvsemproj.models.game.Game;
//import pjvsemproj.models.game.players.HumanPlayer;
//import pjvsemproj.models.game.players.Player;
//import pjvsemproj.models.maps.GameMap;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class GameTest {
//    private static Game game;
//
//    @BeforeAll
//    public static void init() {
//        GameMap map = new GameMap(-15, 15, -15, 15);
//        List<Player> players = new ArrayList<>();
//
//        Player humanPlayer1 = new HumanPlayer("Vasya");
//        players.add(humanPlayer1);
//        Player humanPlayer2 = new HumanPlayer("Misha");
//        players.add(humanPlayer2);
//
//        game = new Game(players, map);
//    }
//
//    @BeforeEach
//    public void refresh() {
//        for (Player player: game.getPlayers()) {
//            player.getCities().clear();
//            player.getTroops().clear();
//            player.setBalance(100);
//        }
//    }
//
//    public Player getFirstPlayer() {
//        return game.getPlayers().getFirst();
//    }
//
//    public Player getSecondPlayer() {
//        return game.getPlayers().getLast();
//    }
//
//    @Test
//    public void addCityOnAnotherCity_doesntAdd() {
//        Player player = getFirstPlayer();
//        player.addCity(new City(-10, -10));
//        player.addCity(new City(-10, -10));
//
//        assertEquals(1, player.getCities().size());
//    }
//
//    @Test
//    public void createTroopUnitOnAnotherTroopUnit_doesntCreate() {
//        Player player = getFirstPlayer();
//        player.addCity(new City(-10, -10));
//
////        new TroopUnit(TroopType.Cavalry, 0, 0);
////        enemy = new TroopUnit(TroopType.Cavalry, 0, 1);
//    }
//
//    @Test
//    public void attackTeamTroops_doesntAttack() {
//
//    }
//
//    @Test
//    public void troopUnitMovesBeyondMap_doesntMove() {
//
//    }
//}
