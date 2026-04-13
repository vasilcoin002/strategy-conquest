package pjvsemproj.controllers;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;
import pjvsemproj.views.GameView;

import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class GameController {

    private final Stage stage;
    // TODO add services instead of changing game directly through the GameController
    private final Game game;
    private final GameView view;

    private TurnManager turnManager;
    private MovementManager movementManager;

    private IGridEntity selectedEntity;

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

        turnManager = new TurnManager(player1, player2);
        movementManager = new MovementManager(game.getMap());
        turnManager.addTurnListener(movementManager);

        turnManager.endTurn();
        turnManager.endTurn();

        view.setOnQuitGameAction(() -> stage.setScene(null));
        view.setOnGameAreaClickedAction(this::handleGameAreaClick);
        view.setOnEntitySelectedAction(this::setSelectedEntity);
    }

    public void showView() {
        view.show();
    }

    private void handleGameAreaClick(int viewX, int viewY) {

        int x = viewX / TILE_SIZE;
        int y = viewY / TILE_SIZE;

        Tile tile = game.getMap().getTile(x, y);

        if (tile.getEntities().isEmpty()) {
            setSelectedEntity(null);
            return;
        }

        IGridEntity entity = tile.getEntities().getFirst();
        setSelectedEntity(entity);
        // TODO extend method handleGameAreaClick that it handles movement clicks
    }

    public void setSelectedEntity(IGridEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
        view.setSelectedEntity(selectedEntity);

        if (selectedEntity instanceof TroopUnit troopUnit) {
            Set<Tile> availableTilesForMovement = movementManager.getAvailableTilesForMovement(troopUnit);
            view.showSelectedEntityAvailableMoves(availableTilesForMovement);
        }
    }
}
