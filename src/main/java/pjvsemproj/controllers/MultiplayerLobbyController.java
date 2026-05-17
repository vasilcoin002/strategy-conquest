package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MultiplayerLobbyView;

/**
 * Controller linking actions inside multiplayer views onto underlying networking clients.
 * <p>
 * Manages configuration updates for network ports and hosts, processes lobby creation logic,
 * handles server connection requests, and displays error logs in the lobby UI.
 */
public class MultiplayerLobbyController {

    private final MultiplayerLobbyView view;
    private final SceneDirector director;
    private final String clientName;

    /**
     * Constructs a lobby controller context instance and binds user interface buttons to their logic closures.
     *
     * @param view       The UI components layer processing multiplayer view definitions and layout elements.
     * @param director   The structural router managing core stage transformations across screens.
     * @param clientName The validated unique username string credential chosen by the player in the main menu.
     */
    public MultiplayerLobbyController(MultiplayerLobbyView view, SceneDirector director, String clientName) {
        this.view = view;
        this.director = director;
        this.clientName = clientName;

        bindActions();
    }

    /**
     * Binds UI buttons to actions.
     * <p>
     * Links functional action listeners onto the execution pathways exposed from the multiplayer lobby view graph.
     */
    private void bindActions() {
        view.setOnCreateGameAction(this::handleCreateGame);
        view.setOnJoinGameAction(this::handleJoinGame);
        view.setOnBackAction(director::showMainMenu);
    }

    /**
     * Extracts parameters to launch an internal server instance and registers a hosting client connection.
     * <p>
     * Parses the current network port text input field. If valid, it triggers a local
     * server socket engine thread daemon on that port and automatically connects the hosting human player into the lobby.
     */
    private void handleCreateGame() {
        int port = parseAndValidatePort();
        if (port == -1) return;

        director.hostLobby(this.clientName, port, this::handleServerConfigError);
    }

    /**
     * Validates connection target strings and registers a connection thread onto external host servers.
     * <p>
     * Extracts the host address configuration text and the network port from view input models.
     * If validation criteria pass, it initializes an asynchronous client socket worker thread to connect to the target host.
     */
    private void handleJoinGame() {
        String host = view.getHost();
        if (host.isEmpty()) {
            view.showError("Host address cannot be empty.");
            return;
        }

        int port = parseAndValidatePort();
        if (port == -1) return;

        director.joinLobby(this.clientName, host, port, this::handleServerConfigError);
    }

    /**
     * Safely extracts the port from the UI, ensuring it is a valid integer
     * within the standard network port range.
     * <p>
     * Clears previous error states, parses input text, and enforces constraints
     * to protect system ports (0-1024) and match standard user-allocatable boundary limits (1025-65535).
     *
     * @return The parsed valid integer port number on success; or {@code -1} if the field is empty, malformed, or out of range.
     */
    private int parseAndValidatePort() {
        view.clearError();
        String portText = view.getPortText();

        if (portText.isEmpty()) {
            view.showError("Port cannot be empty.");
            return -1;
        }

        try {
            int port = Integer.parseInt(portText);
            // System ports are 0-1023. Valid user ports are 1024-65535.
            if (port <= 1024 || port > 65535) {
                view.showError("Port must be between 1025 and 65535.");
                return -1;
            }
            return port;
        } catch (NumberFormatException e) {
            view.showError("Port must be a valid number.");
            return -1;
        }
    }

    /**
     * Dispatches exception reporting strings to the view's red error label UI component.
     * <p>
     * Leverages {@link Platform#runLater(Runnable)} to guarantee thread-safe rendering changes on the main JavaFX application loop
     * when connection or name configuration rejections arrive from background network sockets.
     *
     * @param errorMessage Descriptive error text details passed down from the matchmaking server layer.
     */
    public void handleServerConfigError(String errorMessage) {
        Platform.runLater(() -> view.showError("Server Error: " + errorMessage));
    }
}