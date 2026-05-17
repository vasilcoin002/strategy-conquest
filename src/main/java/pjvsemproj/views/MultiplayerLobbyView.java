package pjvsemproj.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Graphical user interface for the multiplayer matchmaking lobby room.
 * <p>
 * Provides interactive text fields to configure the server host addresses and target network ports,
 * alongside action buttons to initialize a brand-new game session hosting environment or connect to an
 * existing external remote game lobby.
 */
public class MultiplayerLobbyView {

    private final VBox root;

    private final Label errorLabel;
    private final TextField hostField;
    private final TextField portField;

    private Runnable onCreateGameAction;
    private Runnable onJoinGameAction;
    private Runnable onBackAction;

    /**
     * Constructs the multiplayer lobby view layout scene graph and configures visual control nodes.
     * <p>
     * Initializes host and port fields with default values ("localhost" and "4444"), sets up typography weights,
     * builds error alert text indicators, styles navigation buttons, and binds interaction click triggers.
     */
    public MultiplayerLobbyView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30px;");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        hostField = new TextField("localhost");
        hostField.setPromptText("Host");
        hostField.setMaxWidth(300);

        portField = new TextField("4444");
        portField.setPromptText("Port (e.g., 4444)");
        portField.setMaxWidth(300);

        Label title = new Label("MULTIPLAYER LOBBIES");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        Button createBtn = new Button("Create Game");
        Button joinBtn = new Button("Join Selected Lobby");
        Button backBtn = new Button("Back");

        styleButton(createBtn);
        styleButton(joinBtn);
        styleButton(backBtn);

        createBtn.setOnAction(e -> {
            if (onCreateGameAction != null) onCreateGameAction.run();
        });

        joinBtn.setOnAction(e -> {
            if (onJoinGameAction != null) onJoinGameAction.run();
        });

        backBtn.setOnAction(e -> {
            if (onBackAction != null) onBackAction.run();
        });

        root.getChildren().addAll(
                title,
                errorLabel,
                hostField,
                portField,
                createBtn,
                joinBtn,
                backBtn
        );
    }

    /**
     * Applies uniform layout size constraints and text padding styles across a layout button.
     *
     * @param button The target JavaFX {@link Button} asset component to style.
     */
    private void styleButton(Button button) {
        button.setPrefWidth(300);
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
    }

    /**
     * Fetches the root vertical box layout panel holding all multiplayer scene components.
     *
     * @return The JavaFX {@link VBox} layout root pane container node.
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Displays a server configuration, validation, or socket connectivity exception description message.
     *
     * @param errorMessage The descriptive network error message text string to present on screen.
     */
    public void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
    }

    /**
     * Clears out any active exception reporting string details from the lobby error label component.
     */
    public void clearError() {
        errorLabel.setText("");
    }

    /**
     * Registers a callback closure routine task to execute when a hosting player instantiates a lobby.
     *
     * @param action A {@link Runnable} closure containing initialization directives to launch an internal server.
     */
    public void setOnCreateGameAction(Runnable action) {
        this.onCreateGameAction = action;
    }

    /**
     * Registers a callback closure routine task to execute when a remote user attempts to join a lobby host.
     *
     * @param action A {@link Runnable} closure executing socket connection pipelines to connect to external endpoints.
     */
    public void setOnJoinGameAction(Runnable action) {
        this.onJoinGameAction = action;
    }

    /**
     * Registers a callback closure routine task to handle backward main menu screen navigation actions.
     *
     * @param action A {@link Runnable} closure clearing lobby states and restoring primary landing scenes.
     */
    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }

    /**
     * Extracts and sanitizes the destination server target address from the input field models.
     *
     * @return A trimmed text string containing the host loopback or external IPv4 target address.
     */
    public String getHost() {
        return hostField.getText().trim();
    }

    /**
     * Extracts the network port entry configuration text string from the entry field inputs.
     *
     * @return A trimmed raw text representation string of the target communication port index.
     */
    public String getPortText() {
        return portField.getText().trim();
    }
}