package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.views.MultiplayerLobbyView;

public class MultiplayerLobbyController {

    private final MultiplayerLobbyView view;
    private final SceneDirector director;
    private final String clientName;

    private static final int DEFAULT_PORT = 4444;

    public MultiplayerLobbyController(MultiplayerLobbyView view, SceneDirector director, String clientName) {
        this.view = view;
        this.director = director;
        this.clientName = clientName;

        bindActions();
    }

    private void bindActions() {

        view.setOnCreateGameAction(
                this::handleCreateGame
        );

        view.setOnJoinGameAction(
                this::handleJoinGame
        );

        view.setOnBackAction(
                director::showMainMenu
        );
    }

    private void handleCreateGame() {
        director.hostLobby(this.clientName, DEFAULT_PORT);
    }

    private void handleJoinGame() {
        String host = view.getHost();

        director.joinLobby(this.clientName, host, DEFAULT_PORT);
    }

    public void handleServerConfigError(String errorMessage) {
        Platform.runLater(() -> {
            view.showError("Server Error: " + errorMessage);
        });
    }
}