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

        // Check if the service is a LocalGameService to tell the View
        boolean isLocalGame = gameService instanceof LocalGameService;

        GameView gameView = new GameView(
                gameService.getGameDTO(),
                colors,
                isLocalGame
        );
        GameController controller = new GameController(gameService, gameView, this);

        gameView.show(stage);
        stage.centerOnScreen();
        stage.show();
    }

    public void showLocalGame() {
        GameMap map = new GameMap(10, 10);

        Player p1 = new HumanPlayer("Player 1", 100);
        Player p2 = new HumanPlayer("Player 2", 100);

        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.loadGame("savegame.json");

        GameService gameService = new LocalGameService(game);

        showGame(gameService);
    }

    // New Dialog specifically for grabbing the save file path!
    public void showSaveFileDialog(Consumer<String> onFileSelected) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.setInitialFileName("savegame.json");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            onFileSelected.accept(file.getAbsolutePath());
        }
    }
}