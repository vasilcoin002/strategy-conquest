package pjvsemproj.models.game;

import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.GameMap;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Player> players;
    private final GameMap map;

    public Game(List<Player> players, GameMap map) {
        this.players = players;
        this.map = map;
    }

    public Game(GameMap map) {
        this(new ArrayList<>(), map);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) {
        if (players.contains(player)) return false;
        return players.add(player);
    }

    public GameMap getMap() {
        return map;
    }
}
