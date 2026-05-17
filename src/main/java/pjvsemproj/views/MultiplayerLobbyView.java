package pjvsemproj.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MultiplayerLobbyView {

    private final VBox root;

    private Label errorLabel;
    private final TextField hostField;
    private final TextField portField;

    private Runnable onCreateGameAction;
    private Runnable onJoinGameAction;
    private Runnable onBackAction;

    public MultiplayerLobbyView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30px;");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Inside your constructor, around where you define hostField
        hostField = new TextField("localhost");
        hostField.setPromptText("Host");
        hostField.setMaxWidth(300);

        // ADD THIS:
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

    private void styleButton(Button button) {
        button.setPrefWidth(300);
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
    }

    public VBox getRoot() {
        return root;
    }

    public void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    public void setOnCreateGameAction(Runnable action) {
        this.onCreateGameAction = action;
    }

    public void setOnJoinGameAction(Runnable action) {
        this.onJoinGameAction = action;
    }

    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public String getPortText() {
        return portField.getText().trim();
    }
}