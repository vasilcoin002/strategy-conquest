package pjvsemproj.controllers;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;
import pjvsemproj.models.managers.helpers.OwnershipHelper;
import pjvsemproj.views.GameView;

import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

// TODO implement attacking
public class GameController {

    private final Stage stage;
    private final GameView view;

    // TODO add services instead of changing game directly through the GameController
    private final Game game;
    private TurnManager turnManager;
    private MovementManager movementManager;
    private EconomyManager economyManager;
    private CombatManager combatManager;

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

        // TODO add GameController to turn listeners to update next turn button to be enabled
        turnManager = new TurnManager(player1, player2);
        movementManager = new MovementManager(game.getMap());
        economyManager = new EconomyManager();
        combatManager = new CombatManager();
        turnManager.addTurnListener(movementManager);
        turnManager.addTurnListener(economyManager);
        turnManager.addTurnListener(combatManager);

        turnManager.endTurn();
        turnManager.endTurn();

        view.setOnQuitGameAction(() -> stage.setScene(null));
        view.setOnGameAreaClickedAction(this::handleGameAreaClick);
        view.setOnEntitySelectedAction(this::setSelectedEntity);
        view.setOnNextTurnAction(() -> {
//            view.setNextTurnButtonDisabled(true);
            turnManager.endTurn();
        });
    }

    public void showView() {
        view.show();
    }

    public Color getPlayerColor(Player player) {
        if (game.getPlayers().getFirst() == player) return Color.BLUE;
        else return Color.ORANGE;
    }

    private void handleGameAreaClick(int viewX, int viewY) {

        int x = viewX / TILE_SIZE;
        int y = viewY / TILE_SIZE;

        Tile tile = game.getMap().getTile(x, y);

        if (tile.getEntities().isEmpty()) {
            // TODO extend method handleGameAreaClick that it handles movement clicks
            if (selectedEntity instanceof TroopUnit troopUnit) {
                moveSelectedTroop(tile);
            }
            setSelectedEntity(null);
            return;
        }
        else if (tile.getEntities().getFirst() instanceof City city
                && selectedEntity instanceof TroopUnit troopUnit
                && !tile.isBlocked()) {
            moveSelectedTroop(tile);
            // TODO change conquering city manually to conquering through movement manager in service
            conquerCity(troopUnit, city);
        }


        IGridEntity entity = tile.getEntities().getFirst();
        setSelectedEntity(entity);
    }

    private void moveSelectedTroop(Tile toTile) {
        if (selectedEntity instanceof TroopUnit troopUnit) {
            view.clearTroopUnit(troopUnit);
            movementManager.moveTroopUnit(troopUnit, toTile);
            view.updateTroopUnit(troopUnit, getPlayerColor(troopUnit.getOwner()));
        }
    }

    private void conquerCity(TroopUnit conquerer, City city) {
        OwnershipHelper.transferCity(city, conquerer.getOwner());
        view.updateCity(city, getPlayerColor(conquerer.getOwner()));
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
