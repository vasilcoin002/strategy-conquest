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
 * <p>
 * Provides options to start local or multiplayer games. Asserves as the primary landing
 * screen graph for the application interface container, housing text input fields for user identity
 * initialization, action toggles for console logging systems, and exit control configurations.
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

    /**
     * Constructs the main menu layout scene graph and initializes visual control nodes.
     * <p>
     * Assembles title labels, text inputs, error alert boxes, action buttons, and checkboxes.
     * Configures background style sheets, drops focus indicators from input elements on Escape triggers,
     * and forces the container background layout to steal focus immediately upon rendering via {@link Platform#runLater(Runnable)}.
     */
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
        root.setOnMouseClicked(e -> root.requestFocus());
    }

    /**
     * Fetches the structural vertical layout box container holding all menu controls.
     *
     * @return The JavaFX {@link VBox} layout root pane node component.
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Extracts and sanitizes the username text string input into the menu registration field.
     *
     * @return A trimmed string containing the text currently present inside the username input box.
     */
    public String getPlayerName() {
        return playerNameInput.getText().trim();
    }

    /**
     * Allows the Controller to read the state of the checkbox.
     * <p>
     * Queries whether the user wishes to enable console logging properties during match runtime execution blocks.
     *
     * @return {@code true} if the logging checkbox toggle is active and marked; {@code false} if unchecked.
     */
    public boolean isLoggerEnabled() {
        return loggerToggle.isSelected();
    }

    /**
     * Registers a callback closure routine task to trigger when starting a local single-player match.
     *
     * @param action A {@link Runnable} closure containing initialization directives for local games.
     */
    public void setOnLoadLocalGameAction(Runnable action) { this.onLoadLocalGameAction = action; }

    /**
     * Registers a callback closure routine task to trigger when shifting views over to network lobbies.
     *
     * @param action A {@link Runnable} closure containing scene shifting commands to join multiplayer lobbies.
     */
    public void setOnLoadMultiplayerGameAction(Runnable action) { this.onLoadMultiplayerGameAction = action; }

    /**
     * Registers a callback closure routine task to handle application exit triggers.
     *
     * @param action A {@link Runnable} closure executing standard system shutdown sequence commands.
     */
    public void setOnExitAction(Runnable action) { this.onExitAction = action; }

    /**
     * Applies uniform layout constraints and size parameters over a menu button.
     *
     * @param btn The target JavaFX {@link Button} platform asset model to style.
     */
    private void styleButton(Button btn) {
        btn.setPrefWidth(350);
        btn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
    }

    /**
     * Displays a descriptive input alert notification string inside the view layout.
     * <p>
     * Adjusts node management bounds flags so that the red alert text block is added to layout measurements.
     *
     * @param message The descriptive error alert text string to present on screen.
     */
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Clears existing alert string text descriptions and hides the notification label from view structures.
     * <p>
     * Drops visibility parameters and updates layout flags so that the text label consumes zero layout space.
     */
    public void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }
}