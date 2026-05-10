package pjvsemproj.views;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 * Main menu UI of the game.
 *
 * Provides options to start local or multiplayer games.
 */
public class MainMenuView {
    private final VBox root;
    private final CheckBox loggerToggle;
    private final TextField playerNameInput;
    private final Label errorLabel;

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

        playerNameInput = new TextField();
        playerNameInput.setPromptText("Enter your player name (e.g., Vasya)");
        playerNameInput.setMaxWidth(350);
        playerNameInput.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        playerNameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                root.requestFocus(); // make text field drop focus
            }
        });

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button localGameBtn = new Button("Load local game (versus computer)");
        localGameBtn.setOnAction(e -> {
            if (onLoadLocalGameAction != null) onLoadLocalGameAction.run();
        });

        Button multiGameBtn = new Button("Load multiplayer game");
        multiGameBtn.setOnAction(e -> {
            if (onLoadMultiplayerGameAction != null) onLoadMultiplayerGameAction.run();
        });

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

        root.getChildren().addAll(title, playerNameInput, errorLabel, localGameBtn, multiGameBtn, loggerToggle, exitBtn);

        // allow the background to hold focus
        root.setFocusTraversable(true);
        // steal the focus immediately after the scene finishes rendering
        Platform.runLater(root::requestFocus);
        root.setOnMouseClicked(event -> root.requestFocus());
    }

    public VBox getRoot() {
        return root;
    }

    public String getPlayerName() {
        return playerNameInput.getText().trim();
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

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    public void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }
}