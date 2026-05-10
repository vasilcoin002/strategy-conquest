package pjvsemproj.views.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameOverView {

    private final VBox root;
    private final Scene scene;

    private Runnable onMainMenuAction;

    public GameOverView(String winnerName) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: #ff7e67; -fx-border-width: 5px;");

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-text-fill: #ff7e67; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label winnerLabel = new Label(winnerName + " achieves VICTORY!");
        winnerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Button mainMenuBtn = new Button("Return to Main Menu");
        mainMenuBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        mainMenuBtn.setOnAction(e -> {
            if (onMainMenuAction != null) onMainMenuAction.run();
        });

        // Only add the main menu button now
        root.getChildren().addAll(gameOverLabel, winnerLabel, mainMenuBtn);
        scene = new Scene(root, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }

    public void setOnMainMenuAction(Runnable onMainMenuAction) {
        this.onMainMenuAction = onMainMenuAction;
    }
}