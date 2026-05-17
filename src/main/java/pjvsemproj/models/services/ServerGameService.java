package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;

/**
 * Dedicated server-side backend implementation of the referee gameplay service.
 * <p>
 * Extends the basic {@link AbstractGameService} framework to execute simulation rule sets directly
 * over the primary authoritative game state model maintained on the server host.
 */
public class ServerGameService extends AbstractGameService {

    /**
     * Constructs a server-side game service engine context instance.
     *
     * @param game The master domain {@link Game} simulation instance acting as the absolute authority for the match.
     */
    public ServerGameService(Game game) {
        super(game);
    }
}