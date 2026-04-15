package pjvsemproj.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;



/**
 * Main menu UI of the game.
 *
 * Provides options to start local or multiplayer games.
 */

public class MainMenuView {
    private final VBox root;
    private final CheckBox loggerToggle; // The new toggle

    // Callbacks
    private Runnable onLoadLocalGameAction;
    private Runnable onLoadMultiplayerGameAction;
    private Runnable onExitAction;

    public MainMenuView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label title = new Label("STRATEGY CONQUEST");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Button localGameBtn = new Button("Load local game (versus computer)");
        localGameBtn.setOnAction(e -> {
            if (onLoadLocalGameAction != null) onLoadLocalGameAction.run();
        });

        Button multiGameBtn = new Button("Load multiplayer game");
        multiGameBtn.setOnAction(e -> {
            if (onLoadMultiplayerGameAction != null) onLoadMultiplayerGameAction.run();
        });

        // --- THE LOGGER TOGGLE ---
        loggerToggle = new CheckBox("Enable Console Logging");
        loggerToggle.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        loggerToggle.setSelected(true); // Default to checked

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> {
            if (onExitAction != null) onExitAction.run();
        });

        styleButton(localGameBtn);
        styleButton(multiGameBtn);
        styleButton(exitBtn);

        // Add the toggle right above the Exit button
        root.getChildren().addAll(title, localGameBtn, multiGameBtn, loggerToggle, exitBtn);
    }

    public VBox getRoot() {
        return root;
    }

    /**
     * Allows the Controller to read the state of the checkbox.
     */
    public boolean isLoggerEnabled() {
        return loggerToggle.isSelected();
    }

    public void setOnLoadLocalGameAction(Runnable action) { this.onLoadLocalGameAction = action; }
    public void setOnLoadMultiplayerGameAction(Runnable action) { this.onLoadMultiplayerGameAction = action; }
    public void setOnExitAction(Runnable action) { this.onExitAction = action; }

    private void styleButton(Button btn) {
        btn.setPrefWidth(350);
        btn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
    }
}