package pjvsemproj.controllers;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
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

    public SceneDirector(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView();
        MainMenuController menuController = new MainMenuController(menuView, this);

        Scene scene = new Scene(menuView.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Strategy Game - Main Menu");
        stage.show();
        stage.centerOnScreen();
    }

    public void showGame(GameService gameService) {
        List<PlayerDTO> players = gameService.getPlayersDTO();

        Map<String, Color> colors = new HashMap<>();
        colors.put(players.getFirst().name, Color.BLUE);
        colors.put(players.getLast().name, Color.ORANGE);

        GameView gameView = new GameView(
                gameService.getGameDTO(),
                colors
        );

        gameView.show(stage);

        GameController controller = new GameController(gameService, gameView, this);

        stage.centerOnScreen();
        stage.show();
    }

    public void showLocalGame() {
        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.loadGame("config.json");

        GameService gameService = new LocalGameService(game);

        showGame(gameService);
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
}