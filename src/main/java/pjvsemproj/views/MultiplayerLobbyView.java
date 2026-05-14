package pjvsemproj.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MultiplayerLobbyView {

    private final VBox root;
    private final ListView<LobbyInfo> lobbyList;
    private final TextField playerNameField;

    private Runnable onCreateGameAction;
    private Runnable onJoinGameAction;
    private Runnable onBackAction;

    public MultiplayerLobbyView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30px;");

        Label title = new Label("MULTIPLAYER LOBBIES");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        playerNameField = new TextField("Player");
        playerNameField.setPromptText("Player name");
        playerNameField.setMaxWidth(300);

        lobbyList = new ListView<>();
        lobbyList.setPrefSize(400, 250);
        lobbyList.setPlaceholder(new Label("No lobbies available"));

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
                playerNameField,
                lobbyList,
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

    public String getPlayerName() {
        return playerNameField.getText().trim();
    }

    public LobbyInfo getSelectedLobby() {
        return lobbyList.getSelectionModel().getSelectedItem();
    }

    public void addLobby(LobbyInfo lobbyInfo) {
        lobbyList.getItems().add(lobbyInfo);
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
}