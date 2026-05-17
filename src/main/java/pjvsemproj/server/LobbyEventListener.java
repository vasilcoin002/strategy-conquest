package pjvsemproj.server;


import javafx.application.Platform;
import pjvsemproj.controllers.SceneDirector;

public class LobbyEventListener {

    private final SceneDirector director;
    private final Client client;
    private final String playerName;

    public LobbyEventListener(
            SceneDirector director,
            Client client,
            String playerName
    ) {
        this.director = director;
        this.client = client;
        this.playerName = playerName;
    }

    public void onGameState(String json) {
        Platform.runLater(() -> {
            director.openNetworkGameFromJson(client, playerName, json);
        });
    }
}