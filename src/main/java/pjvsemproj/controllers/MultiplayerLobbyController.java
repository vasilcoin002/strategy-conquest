package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MultiplayerLobbyView;

// TODO fix loading with wrong config
public class MultiplayerLobbyController {

    private final MultiplayerLobbyView view;
    private final SceneDirector director;
    private final String clientName;

    public MultiplayerLobbyController(MultiplayerLobbyView view, SceneDirector director, String clientName) {
        this.view = view;
        this.director = director;
        this.clientName = clientName;

        bindActions();
    }

    private void bindActions() {
        view.setOnCreateGameAction(this::handleCreateGame);
        view.setOnJoinGameAction(this::handleJoinGame);
        view.setOnBackAction(director::showMainMenu);
    }

    private void handleCreateGame() {
        int port = parseAndValidatePort();
        if (port == -1) return;

        director.hostLobby(this.clientName, port, this::handleServerConfigError);
    }

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
     * @return the valid port number, or -1 if invalid.
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

    public void handleServerConfigError(String errorMessage) {
        Platform.runLater(() -> {
            view.showError("Server Error: " + errorMessage);
        });
    }
}