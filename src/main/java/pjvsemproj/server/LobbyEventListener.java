package pjvsemproj.server;


import javafx.application.Platform;
import pjvsemproj.controllers.SceneDirector;

import java.util.function.Consumer;

public class LobbyEventListener {

    private final SceneDirector director;
    private final Client client;
    private final String playerName;

    private final Consumer<String> onErrorCallback;

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

    public void onGameState(String json) {
        Platform.runLater(() -> director.openNetworkGameFromJson(client, playerName, json));
    }

    public void onError(String errorMessage) {
        if (onErrorCallback != null) {
            Platform.runLater(() -> onErrorCallback.accept(errorMessage));
        }
    }
}