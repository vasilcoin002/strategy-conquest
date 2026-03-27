package pjvsemproj.models.game;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.maps.GameMap;

import java.util.ArrayList;
import java.util.List;

// TODO refactor class to managers
public class Game {
    private final List<Player> players;
    private final GameMap map;
    private int currentTurnIndex;

    public Game(List<Player> players, GameMap map) {
        this.players = players;
        this.map = map;
        this.currentTurnIndex = 0;
    }

    public Game(GameMap map) {
        this(new ArrayList<>(), map);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public GameMap getMap() {
        return map;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

//    // TODO finish nextTurn
//    public void nextTurn() {
//        if (players.isEmpty()) return;
//
//        currentTurnIndex = (currentTurnIndex + 1) % players.size();
//    }

    // TODO move method to manager
    private Player findCityOwner(City city) {
        Player cityOwner = null;
        for (Player player: players) {
            if (player.getCities().contains(city)) {
                cityOwner = player;
                break;
            }
        }
        return cityOwner;
    }

    // TODO move method to manager
    public void surrenderCityTo(City city, Player enemy) {
        Player cityOwner = findCityOwner(city);
        if (cityOwner != null) {
            cityOwner.removeCity(city);
            enemy.addCity(city);
        }

//        if (isGameOver()) {
//            Player winner = getWinner();
//            // TODO: Trigger game over UI/Logic here using the winner
//            System.out.println("Game Over! The winner is: " + winner.getName());
//        }
    }

    // TODO move method to manager
    public void setCurrentTurnIndex(int currentTurnIndex) {
        if (currentTurnIndex < 0 || currentTurnIndex > players.size()) {
            throw new IllegalArgumentException("Index is beyond of players list");
        }
        this.currentTurnIndex = currentTurnIndex;
    }
}
