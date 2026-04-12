package pjvsemproj.controllers;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;
import pjvsemproj.views.GameView;

public class GameController {

    private final Stage stage;
    // TODO add services instead of changing game directly through the GameController
    private final Game game;
    private final GameView view;

    public GameController(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.view = new GameView(
                stage,
                game.getMap().getWidth(),
                game.getMap().getHeight(),
                game.getPlayers(),
                game.getPlayers().getFirst(),
                Color.BLUE,
                Color.ORANGE
        );

        Player player1 = game.getPlayers().getFirst();
        Player player2 = game.getPlayers().getLast();

        TurnManager turnManager = new TurnManager(player1, player2);
        MovementManager movementManager = new MovementManager(game.getMap());
        turnManager.addTurnListener(movementManager);

        turnManager.endTurn();
        turnManager.endTurn();

        // TODO transfer action settings to controller
//        view.setOnEntitySelectedAction(gameView::setSelectedEntity);
        view.setOnQuitGameAction(() -> stage.setScene(null));
    }

    public void showView() {
        view.show();
    }
}
