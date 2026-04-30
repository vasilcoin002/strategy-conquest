package pjvsemproj.models.game;

import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.game.maps.GameMap;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a complete game state.
 *
 * Contains the list of players and the map on which the match is played.
 */
public class Game {
    private final List<Player> players;
    private Player currentPlayer;
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

    /**
     * Adds a player to the game.
     *
     * @param player player to add
     * @return {@code true} if the player was added successfully
     */
    public boolean addPlayer(Player player) {
        if (players.contains(player)) return false;
        return players.add(player);
    }

    public GameMap getMap() {
        return map;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
