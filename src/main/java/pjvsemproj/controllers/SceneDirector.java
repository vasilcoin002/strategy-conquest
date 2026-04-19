package pjvsemproj.controllers;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;
import pjvsemproj.views.GameView;
import pjvsemproj.views.MainMenuView;


/**
 * Responsible for switching between different application scenes.
 *
 * Acts as a central navigation manager between:
 * - Main menu
 * - Game screen
 *
 * Encapsulates JavaFX Stage manipulation and scene creation.
 */
public class SceneDirector {
    private final Stage stage;

    public SceneDirector(Stage stage) {
        this.stage = stage;
    }
    /**
     * Displays the main menu scene.
     *
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
    }
    /**
     * Displays the game scene.
     *
     * Initializes:
     * - GameView based on provided Game object
     * - (optionally) GameController
     *
     * @param game initialized game instance
     */
    public void showGame(Game game) {
        // This relies on your GameSetupManager to provide the initialized Game object
        GameView gameView = new GameView(
                stage,
                game.getMap().getWidth(),
                game.getMap().getHeight(),
                game.getPlayers(),
                game.getPlayers().getFirst(),
                Color.BLUE,
                Color.ORANGE
        );

        gameView.show();

        // Initialize your game controllers here...
        // GameController controller = new GameController(game, gameView, ...);

        Scene scene = new Scene(gameView.getRoot(), 1200, 800);
        stage.setScene(scene);
    }
}
