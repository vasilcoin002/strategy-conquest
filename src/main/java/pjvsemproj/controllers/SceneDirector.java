package pjvsemproj.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pjvsemproj.config.EntityDTODeserializer;
import pjvsemproj.config.GameSetupManager;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.services.ClientGameEngine;
import pjvsemproj.models.services.LocalGameService;
import pjvsemproj.models.services.NetworkGameService;
import pjvsemproj.server.Client;
import pjvsemproj.server.GameServer;
import pjvsemproj.server.LobbyEventListener;
import pjvsemproj.views.MainMenuView;
import pjvsemproj.views.MultiplayerLobbyView;
import pjvsemproj.views.game.GameOverView;
import pjvsemproj.views.game.GameView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Responsible for switching between different application scenes.
 */
public class SceneDirector {
    private final Stage stage;
    private Thread serverThread;

    public SceneDirector(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView();
        new MainMenuController(menuView, this, menuView.getPlayerName());

        Scene scene = new Scene(menuView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Main Menu");
        stage.show();
        stage.centerOnScreen();
    }

    public void showNetworkGame(ClientGameEngine gameService, String clientName) {
        initializeAndShowGameView(gameService, clientName);
    }

    public void showGame(ClientGameEngine gameService, String clientName) {
        gameService.login(clientName);
        initializeAndShowGameView(gameService, clientName);
    }

    /**
     * Shared helper method to avoid duplication. Handles DTO mapping,
     * color assignments, controller binding, and JavaFX stage rendering.
     */
    private void initializeAndShowGameView(ClientGameEngine gameService, String clientName) {
        List<PlayerDTO> players = gameService.getPlayersDTO();

        Map<String, Color> colors = new HashMap<>();
        colors.put(players.getFirst().name, Color.BLUE);
        colors.put(players.getLast().name, Color.ORANGE);

        GameView gameView = new GameView(
                gameService.getGameDTO(),
                colors
        );
        gameView.show(stage, clientName);

        GameController controller = new GameController(gameService, gameView, this);
        controller.initialize();

        stage.centerOnScreen();
        stage.show();
    }

    public void showLocalGame(String clientName) {
        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.loadLocalGame("config.json", clientName);

        ClientGameEngine gameService = new LocalGameService(game);
        showGame(gameService, clientName);
    }

    public void showSaveFileDialog(Consumer<String> onFileSelected) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.setInitialFileName("config.json");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            onFileSelected.accept(file.getAbsolutePath());
        }
    }

    public void showGameOverPopup(String winnerName) {
        GameOverView popupView = new GameOverView(winnerName);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(this.stage);
        popupStage.setTitle("Game Over");
        popupStage.setScene(popupView.getScene());

        // ADD THIS LINE: Completely disables the "X" close button
        popupStage.setOnCloseRequest(Event::consume);

        popupView.setOnMainMenuAction(() -> {
            popupStage.close();
            showMainMenu();
        });

        popupStage.showAndWait();
    }

    public void showMultiplayerLobby(String playerName) {
        MultiplayerLobbyView lobbyView = new MultiplayerLobbyView();
        new MultiplayerLobbyController(lobbyView, this, playerName);

        Scene scene = new Scene(lobbyView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Multiplayer Lobby");
        stage.show();
        stage.centerOnScreen();
    }

    public void startServer(int port) {
        if (serverThread != null && serverThread.isAlive()) {
            System.out.println("Server already running.");
            return;
        }

        GameServer server = new GameServer(port);
        serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("Server started on port " + port);
    }

    public void joinLobby(String playerName, String host, int port, Consumer<String> onError) {
        System.out.println(playerName + " joins " + host + ":" + port);

        Client client = new Client(host, port, playerName);

        client.setLobbyListener(new LobbyEventListener(this, client, playerName, onError));

        Thread clientThread = new Thread(client);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public void hostLobby(String playerName, int port, Consumer<String> onError) {
        startServer(port);
        joinLobby(playerName, "localhost", port, onError);
    }

    public void openNetworkGameFromJson(Client client, String playerName, String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EntityDTO.class, new EntityDTODeserializer())
                .create();

        GameDTO dto = gson.fromJson(json, GameDTO.class);

        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.createNetworkGameFromDTO(dto, playerName);

        NetworkGameService gameService = new NetworkGameService(client, game);
        gameService.setLocalClientName(playerName);

        showNetworkGame(gameService, playerName);
    }
}