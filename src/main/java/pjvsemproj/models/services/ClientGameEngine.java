package pjvsemproj.models.services;

import java.util.function.Consumer;

/**
 * Specialization interface extending the core service to provide local application container client operations.
 * <p>
 * Binds presentation controllers to network connection modules, manages authorization keys,
 * tracks local permission metrics, handles disconnection pipelines, and registers async event callbacks.
 */
public interface ClientGameEngine extends CoreGameService {

    /**
     * Authenticates and registers a player identity credential string within the client-side execution framework.
     *
     * @param playerName The custom username selected by the human participant.
     */
    void login(String playerName);

    /**
     * Dispatches ready status markers across communication interfaces to signify the UI has rendered
     * and the client is ready for match commencement.
     */
    void ready();

    /**
     * Fetches the authenticated registration username tracking token bound to this specific instance context.
     *
     * @return The local client username identifier string.
     */
    String getClientName();

    /**
     * Verifies whether the local client profile username matches the turn clearance key specified by the service layer.
     * <p>
     * Used by display managers to dynamically enable or toggle interactive controls and buttons.
     *
     * @return {@code true} if the current active turn identity matches the client name; {@code false} if input commands should lock.
     */
    boolean isMyTurn();

    /**
     * Shuts down internal socket infrastructure, clears references, closes background listener loops,
     * and exits back to primary application spaces.
     */
    void quit();

    /**
     * Registers a callback closure to be triggered whenever an end-of-match state pattern is verified by the backend.
     *
     * @param callback A {@link Consumer} closure receiving a string containing the profile name of the winning player.
     */
    void setOnGameOver(Consumer<String> callback);

    /**
     * Registers a listener routine task to be triggered whenever layout states undergo structural modifications.
     * <p>
     * Used to prompt the JavaFX graphics timeline to initiate cell redrawing sweeps.
     *
     * @param callback A {@link Runnable} task closure executed immediately upon grid update events.
     */
    void setOnBoardUpdated(Runnable callback);

    /**
     * Explicitly sets the internal username identifier context for this engine layer instance.
     *
     * @param playerName The user profile name string to store as the local client owner identity.
     */
    void setLocalClientName(String playerName);
}