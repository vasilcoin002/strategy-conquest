package pjvsemproj.models.game.players;

/**
 * Represents an AI-controlled player.
 *
 * Uses managers to evaluate actions such as moving, attacking,
 * buying units and upgrading cities.
 */
public class BotPlayer extends Player {

    public BotPlayer(String name, int initialBalance) {
        super(name, initialBalance);
    }

}
