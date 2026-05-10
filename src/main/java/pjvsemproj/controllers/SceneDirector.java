package pjvsemproj.controllers;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pjvsemproj.config.GameSetupManager;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.services.ClientGameEngine;
import pjvsemproj.models.services.LocalGameService;
import pjvsemproj.views.MainMenuView;
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

    public void showLocalGame() {
        String myName = "Vasya"; // TODO this has to come from a text field

        GameSetupManager setupManager = new GameSetupManager();
        Game game = setupManager.loadLocalGame("config.json", myName);

        ClientGameEngine gameService = new LocalGameService(game);
        showGame(gameService, myName);
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
}