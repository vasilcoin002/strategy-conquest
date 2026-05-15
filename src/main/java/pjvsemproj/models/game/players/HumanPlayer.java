package pjvsemproj.models.game.players;

/**
 * Represents a human-controlled player.
 */
public class HumanPlayer extends Player {

    /**
     * Constructs a new human player instance.
     *
     * @param name           the display name of the player
     * @param initialBalance the starting amount of gold
     */
    public HumanPlayer(String name, int initialBalance) {
        super(name, initialBalance);
    }
}