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
import java.util.logging.Logger;

/**
 * Structural coordinator class implementing the Application Controller and Router patterns.
 * <p>
 * Centralizes view navigation by directly managing the primary JavaFX {@link Stage} context.
 * Orchestrates window initialization pipelines, loads maps from configurations, manages file selection dialogs,
 * and tracks server background threads safely.
 */
public class SceneDirector {
    private static final Logger LOGGER = Logger.getLogger(SceneDirector.class.getName());
    private final Stage stage;
    private Thread serverThread;

    /**
     * Constructs a manager context pinned to a root application stage window wrapper.
     *
     * @param stage The primary execution window wrapper platform managed by this director.
     */
    public SceneDirector(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes components and loads the primary Main Menu screen graph into view scopes.
     * <p>
     * Creates a new {@link MainMenuView} and hooks up its corresponding {@link MainMenuController}
     * before swapping the root scene and centering the window wrapper on the user's screen.
     */
    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView();
        new MainMenuController(menuView, this, menuView.getPlayerName());

        Scene scene = new Scene(menuView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Main Menu");
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Entry point for rendering multiplayer match interfaces from a remote server session.
     * <p>
     * Directly delegates to the inner shared initialization helper without triggering
     * a local login procedure.
     *
     * @param gameService The logic processing engine proxy service bound to active network sockets.
     * @param clientName  The unique username identifier used by the local user.
     */
    public void showNetworkGame(ClientGameEngine gameService, String clientName) {
        initializeAndShowGameView(gameService, clientName);
    }

    /**
     * Entry point for rendering single-player local match interfaces.
     * <p>
     * Registers and logs the player credentials into the client-side game engine simulation
     * before dispatching layout rendering sequences.
     *
     * @param gameService The logic processing engine tracking the local simulation data properties.
     * @param clientName  The unique username identifier used by the local human player.
     */
    public void showGame(ClientGameEngine gameService, String clientName) {
        gameService.login(clientName);
        initializeAndShowGameView(gameService, clientName);
    }

    /**
     * Shared helper method to avoid duplication. Handles DTO mapping,
     * color assignments, controller binding, and JavaFX stage rendering.
     * <p>
     * Extracts active player structures, sets up matching map-color keys, initializes the
     * graphical {@link GameView}, binds a dedicated {@link GameController}, and commands the stage to display.
     *
     * @param gameService The underlying active engine simulation service context.
     * @param clientName  The player identity associated with this client execution layer.
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

    /**
     * Loads a single-player level layout configuration from storage and launches a local match session.
     * <p>
     * Invokes the {@link GameSetupManager} parsing pipeline to safely read "config.json", provisions
     * an operational {@link LocalGameService} instance, and requests interface initialization.
     *
     * @param clientName The validated username string used by the human participant.
     */
    public void showLocalGame(String clientName) {
        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.loadLocalGame("config.json", clientName);

        ClientGameEngine gameService = new LocalGameService(game);
        showGame(gameService, clientName);
    }

    /**
     * Renders a native modal file selection window to extract path destinations for exporting level state files.
     * <p>
     * Constrains inputs to match standard extension parameters (".json") and passes valid path strings
     * back through execution callbacks.
     *
     * @param onFileSelected Callback functional closure consumer triggered with the absolute file path destination upon success.
     */
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

    /**
     * Renders a modal popup overlay intercepting interface interaction inputs when end-of-match states occur.
     * <p>
     * Configures modular safety behaviors, blocks standard OS application window "X" termination buttons
     * via event consumption, and provides navigation loops to return to the root screen safely.
     *
     * @param winnerName The unique username string identifying the victorious participant.
     */
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

    /**
     * Initializes components and displays the multiplayer connection lobby screen graph.
     * <p>
     * Wire up a new {@link MultiplayerLobbyView} alongside its binding controller before
     * updating stage viewport boundaries.
     *
     * @param playerName Username credential tracking token passed down from parent views.
     */
    public void showMultiplayerLobby(String playerName) {
        MultiplayerLobbyView lobbyView = new MultiplayerLobbyView();
        new MultiplayerLobbyController(lobbyView, this, playerName);

        Scene scene = new Scene(lobbyView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Multiplayer Lobby");
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Provisions and runs an isolated host server engine context running over a background daemon thread structure.
     * <p>
     * Checks if a server thread is already alive before attempting instantiation to prevent address allocation conflicts.
     *
     * @param port The target network address index port number where inbound connection sockets will be received.
     */
    public void startServer(int port) {
        if (serverThread != null && serverThread.isAlive()) {
            LOGGER.warning("Server already running.");
            return;
        }

        GameServer server = new GameServer(port);
        serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();

        LOGGER.info("Server started on port " + port);
    }

    /**
     * Instantiates an asynchronous client context worker and hooks up background message listeners.
     * <p>
     * Initializes communication layers, assigns a dedicated {@link LobbyEventListener} callback trap,
     * and boots up background execution threads.
     *
     * @param playerName Local credential string tracking username registration parameters.
     * @param host       Target connection IPv4 loopback or remote host server socket string.
     * @param port       Target network communication destination port address index.
     * @param onError    Callback handler closure to route remote registration rejection reports back onto lobby errors.
     */
    public void joinLobby(String playerName, String host, int port, Consumer<String> onError) {
        LOGGER.info(playerName + " joins " + host + ":" + port);

        Client client = new Client(host, port, playerName);

        client.setLobbyListener(new LobbyEventListener(this, client, playerName, onError));

        Thread clientThread = new Thread(client);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    /**
     * Automated composition utility method to spin up a local server node and link a hosting connection on localhost.
     *
     * @param playerName Local credential string tracking hosting user registration parameters.
     * @param port       The target network address port index number.
     * @param onError    Callback handler closure to report configuration rejections back to the screen.
     */
    public void hostLobby(String playerName, int port, Consumer<String> onError) {
        startServer(port);
        joinLobby(playerName, "localhost", port, onError);
    }

    /**
     * Deserializes raw string packets transferred from remote servers into full structural game configurations.
     * <p>
     * Instantiates an active {@link Gson} parser injected with matching type deserializers, rebuilds
     * structural maps via {@link GameSetupManager}, binds network service proxies, and renders match windows.
     *
     * @param client     The tracking background simulation networking client connection node.
     * @param playerName Unique identity token credentials assigned to the local application match player.
     * @param json       Raw serialized data string transferred over socket input streams.
     */
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