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
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
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
    private GameServer server;
    private Thread serverThread;

    public SceneDirector(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView();
        MainMenuController menuController = new MainMenuController(menuView, this, menuView.getPlayerName());

        Scene scene = new Scene(menuView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Main Menu");
        stage.show();
        stage.centerOnScreen();
    }

    public void showGame(ClientGameEngine gameService, String clientName) {
        gameService.login(clientName);
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

    public void showMultiplayerGame(String clientName) {
        // TODO: In the future, this is where you will instantiate your
        // Client object, connect to the Server, and then create the NetworkGameService!

        System.out.println("Connecting to network game as: " + clientName);
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
        MultiplayerLobbyController lobbyController = new MultiplayerLobbyController(lobbyView, this, playerName);

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

        server = new GameServer(port);
        serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("Server started on port " + port);
    }

    public void joinLobby(String playerName, String host, int port) {
        System.out.println(playerName + " joins " + host + ":" + port);

        Client client = new Client(host, port, playerName);
        client.setLobbyListener(new LobbyEventListener(this, client, playerName));
        Thread clientThread = new Thread(client);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public void openNetworkGame(
            Client client,
            String myName,
            String player1Name,
            String player2Name
    ) {
        Player p1 = new HumanPlayer(player1Name, 100);
        Player p2 = new HumanPlayer(player2Name, 100);

        GameSetupManager setupManager = new GameSetupManager();
        GameMap map = new GameMap(5, 5);
        Game game = setupManager.setupTestMatch(map, p1, p2);

        NetworkGameService gameService =
                new NetworkGameService(client, game);

        gameService.setLocalClientName(myName);

        showNetworkGame(gameService, myName);

        gameService.ready();
    }

    public void hostLobby(String playerName, int port) {
        startServer(port);

        joinLobby(playerName, "localhost", port);
    }

    public void showNetworkGame(ClientGameEngine gameService, String clientName) {
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