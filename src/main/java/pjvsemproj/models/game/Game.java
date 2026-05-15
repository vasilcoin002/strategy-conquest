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

    /**
     * Constructs a Game state with a pre-existing list of players and a map.
     *
     * @param players the initial list of players
     * @param map     the game map
     */
    public Game(List<Player> players, GameMap map) {
        this.players = players;
        this.map = map;
    }

    /**
     * Constructs a Game state with an empty list of players and a specific map.
     *
     * @param map the game map
     */
    public Game(GameMap map) {
        this(new ArrayList<>(), map);
    }

    /**
     * Retrieves the list of all players currently in the game.
     *
     * @return the list of players
     */
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

    /**
     * Retrieves the game map.
     *
     * @return the map object
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Retrieves the player whose turn is currently active.
     *
     * @return the active player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the player whose turn is currently active.
     *
     * @param currentPlayer the player to set as active
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}