package pjvsemproj.models.services;

import java.util.function.Consumer;

/**
 * Defines the client-specific operations required to participate in a local or network game session.
 * <p>
 * Extends {@link CoreGameService} to provide lifecycle, networking, and UI-binding capabilities
 * such as authenticating, declaring readiness, and registering event callbacks.
 */
public interface ClientGameEngine extends CoreGameService {

    /**
     * Registers the client's chosen display name with the game server or local session.
     *
     * @param playerName the name the user wishes to be identified by
     */
    void login(String playerName);

    /**
     * Signals to the game host that this client has finished loading and is ready to begin the match.
     */
    void ready();

    /**
     * Retrieves the name currently registered to this client instance.
     *
     * @return the client's player name
     */
    String getClientName();

    /**
     * Verifies if the local client is currently allowed to issue game commands.
     *
     * @return {@code true} if it is this client's turn to play
     */
    boolean isMyTurn();

    /**
     * Gracefully disconnects the client from the game session and cleans up resources.
     */
    void quit();

    /**
     * Binds a callback function that will be executed when the game reaches a victory state.
     *
     * @param callback a consumer function that accepts the winning player's name as an argument
     */
    void setOnGameOver(Consumer<String> callback);

    /**
     * Binds a callback function that will be executed whenever the internal game state changes.
     * Typically used to trigger UI repaints (e.g., refreshing the map and side panel).
     *
     * @param callback a runnable task to execute upon state updates
     */
    void setOnBoardUpdated(Runnable callback);
}