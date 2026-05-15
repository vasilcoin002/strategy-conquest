package pjvsemproj.models.game.players;

/**
 * Represents an AI-controlled player.
 * <p>
 * Uses managers to evaluate actions such as moving, attacking,
 * buying units and upgrading cities.
 */
public class BotPlayer extends Player {

    /**
     * Constructs a new AI-controlled player instance.
     *
     * @param name           the internal or display name of the bot
     * @param initialBalance the starting amount of gold
     */
    public BotPlayer(String name, int initialBalance) {
        super(name, initialBalance);
    }

}