package pjvsemproj.server;

import javafx.application.Platform;
import pjvsemproj.controllers.SceneDirector;
import java.util.function.Consumer;

/**
 * Event listener responsible for handling network updates during the pre-game lobby and matchmaking phase.
 * <p>
 * This class acts as a bridge between the asynchronous background networking client ({@link Client})
 * and the main JavaFX application UI thread managed by the {@link SceneDirector}.
 */
public class LobbyEventListener {

    private final SceneDirector director;
    private final Client client;
    private final String playerName;
    private final Consumer<String> onErrorCallback;

    /**
     * Constructs a lobby event listener with its required routing dependencies and error handlers.
     *
     * @param director        The application's scene director responsible for switching active screens.
     * @param client          The background network communication client worker.
     * @param playerName      The authenticated unique username credential chosen by the local player.
     * @param onErrorCallback A functional callback consumer used to route server errors back to the lobby view's error label.
     */
    public LobbyEventListener(
            SceneDirector director,
            Client client,
            String playerName,
            Consumer<String> onErrorCallback
    ) {
        this.director = director;
        this.client = client;
        this.playerName = playerName;
        this.onErrorCallback = onErrorCallback;
    }

    /**
     * Handles the receipt of an authoritative game state snapshot from the server when a match begins.
     * <p>
     * Wraps execution in {@link Platform#runLater(Runnable)} to safely transition the application
     * scene graph from the lobby interface over to the interactive game board view on the main JavaFX thread.
     *
     * @param json The raw serialized JSON configuration string sent by the server representing the initial map and entities layout.
     */
    public void onGameState(String json) {
        Platform.runLater(() -> director.openNetworkGameFromJson(client, playerName, json));
    }

    /**
     * Processes server-side configuration rejections or validation errors.
     * <p>
     * Leverages {@link Platform#runLater(Runnable)} to safely forward the error text back to the visual components
     * of the active controller without causing thread coordination panics.
     *
     * @param errorMessage The descriptive error text reason sent by the server (e.g., name taken or invalid config).
     */
    public void onError(String errorMessage) {
        if (onErrorCallback != null) {
            Platform.runLater(() -> onErrorCallback.accept(errorMessage));
        }
    }
}