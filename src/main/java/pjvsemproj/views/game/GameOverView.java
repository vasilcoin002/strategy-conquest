package pjvsemproj.views.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Graphical layout representation modal presented upon match resolution.
 * <p>
 * Displays the victory declaration banner identifying the winning participant and offers
 * interactive controls to cleanly exit the active match session back to the primary menu.
 */
public class GameOverView {

    private final Scene scene;

    private Runnable onMainMenuAction;

    /**
     * Constructs a game over layout node graph hydrated with the winning player's credentials.
     * <p>
     * Builds standard layout dimensions, style themes, typography weights, and registers
     * an event trigger on the return action button.
     *
     * @param winnerName The unique profile username string identifying the victorious player.
     */
    public GameOverView(String winnerName) {
        VBox root = new VBox(20);
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

    /**
     * Fetches the self-contained window scene graph prepared for modal display staging.
     *
     * @return The pre-assembled JavaFX {@link Scene} container asset.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Registers a callback closure to execute root menu redirections when requested by user input.
     *
     * @param onMainMenuAction A {@link Runnable} closure containing navigation instructions.
     */
    public void setOnMainMenuAction(Runnable onMainMenuAction) {
        this.onMainMenuAction = onMainMenuAction;
    }
}