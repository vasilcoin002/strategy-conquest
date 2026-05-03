package pjvsemproj.controllers;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.config.GameSetupManager;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.HumanPlayer;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.services.GameService;
import pjvsemproj.models.services.LocalGameService;
import pjvsemproj.views.MainMenuView;
import pjvsemproj.views.game.GameView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for switching between different application scenes.
 * <p>
 * Acts as a central navigation manager between:
 * - Main menu
 * - Game screen
 * <p>
 * Encapsulates JavaFX Stage manipulation and scene creation.
 */
public class SceneDirector {
    private final Stage stage;

    public SceneDirector(Stage stage) {
        this.stage = stage;
    }

    /**
     * Displays the main menu scene.
     * <p>
     * Initializes:
     * - MainMenuView
     * - MainMenuController
     * and sets them into the stage.
     */
    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView();
        MainMenuController menuController = new MainMenuController(menuView, this);

        Scene scene = new Scene(menuView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Main Menu");
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Displays the game scene.
     */
    public void showGame(GameService gameService) {
        List<PlayerDTO> players = gameService.getPlayersDTO();

        Map<String, Color> colors = new HashMap<>();
        colors.put(players.getFirst().name, Color.BLUE);
        colors.put(players.getLast().name, Color.ORANGE);
        // This relies on your GameSetupManager to provide the initialized Game object
        GameView gameView = new GameView(
                gameService.getGameDTO(),
                colors
        );

        gameView.show(stage);

        // Initialize game controller here...
        GameController controller = new GameController(gameService, gameView);

        stage.centerOnScreen();
        stage.show();
    }

    public void showLocalGame() {
        GameMap map = new GameMap(10, 10);

        Player p1 = new HumanPlayer("Player 1", 100);
        Player p2 = new HumanPlayer("Player 2", 100);

        GameSetupManager setupManager = new GameSetupManager();
//        Game game = setupManager.setupTestMatch(map, p1, p2);
        Game game = setupManager.loadGame("config.json");

        GameService gameService = new LocalGameService(game);

        showGame(gameService);
    }

}
