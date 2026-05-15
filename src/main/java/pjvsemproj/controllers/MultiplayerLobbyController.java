package pjvsemproj.controllers;

import pjvsemproj.views.MultiplayerLobbyView;

public class MultiplayerLobbyController {

    private final MultiplayerLobbyView view;
    private final SceneDirector director;

    private static final int DEFAULT_PORT = 5000;

    public MultiplayerLobbyController(MultiplayerLobbyView view, SceneDirector director) {
        this.view = view;
        this.director = director;

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

        String playerName =
                view.getPlayerName();

        director.hostLobby(playerName, DEFAULT_PORT);
    }

    private void handleJoinGame() {

        String playerName =
                view.getPlayerName();

        String host = view.getHost();

        director.joinLobby(playerName, host, DEFAULT_PORT);
    }
}