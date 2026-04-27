package pjvsemproj.controllers;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.models.services.GameService;
import pjvsemproj.views.game.GameView;

import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;


/**
 * Main controller connecting UI with game logic.
 *
 * Handles:
 * - user input
 * - selection
 * - movement
 * - UI updates
 */
// TODO implement attacking
// TODO add movement and attack range info to side panel entity info
public class GameController {

    private final Stage stage;
    private final GameView view;

    // TODO add services instead of changing game directly through the GameController
    private final GameService gameService;

    private IGridEntity selectedEntity;

    public GameController(Stage stage, GameService gameService) {
        this.stage = stage;
        this.gameService = gameService;
        this.view = new GameView(
                stage,
                gameService.getMap().getWidth(),
                gameService.getMap().getHeight(),
                gameService.getPlayers(),
                gameService.getPlayers().getFirst(),
                Color.BLUE,
                Color.ORANGE
        );

        Player player1 = gameService.getPlayers().getFirst();
        Player player2 = gameService.getPlayers().getLast();

        view.setOnQuitGameAction(() -> stage.setScene(null));
        view.setOnGameAreaClickedAction(this::handleGameAreaClick);
        view.setOnEntitySelectedAction(this::setSelectedEntity);
        view.setOnNextTurnAction(() -> {
//            view.setNextTurnButtonDisabled(true);
            gameService.endTurn();
            view.updatePlayersBalance(gameService.getPlayers());
            view.updateCurrentPlayer(gameService.getCurrentPlayer());
            setSelectedEntity(selectedEntity);
        });
    }

    public void showView() {
        view.show();
    }

    public Color getPlayerColor(Player player) {
        if (gameService.getPlayers().getFirst() == player) return Color.BLUE;
        else return Color.ORANGE;
    }
    /**
     * Handles click on game map.
     */
    private void handleGameAreaClick(int viewX, int viewY) {

        int x = viewX / TILE_SIZE;
        int y = viewY / TILE_SIZE;

        Tile tile = gameService.getMap().getTile(x, y);

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
            // TODO change conquering city manually to conquering through conquering manager in service
            conquerCity(troopUnit, city);
        }


        IGridEntity entity = tile.getEntities().getLast();
        setSelectedEntity(entity);
    }
    /**
     * Moves selected troop unit.
     */
    private void moveSelectedTroop(Tile toTile) {
        if (selectedEntity instanceof TroopUnit troopUnit) {
            view.clearTroopUnit(troopUnit);
            gameService.moveUnit(troopUnit.getId(), toTile.getX(), toTile.getY());
            view.updateTroopUnit(troopUnit, getPlayerColor(troopUnit.getOwner()));
        }
    }
    /**
     * Handles city conquest.
     */
    private void conquerCity(TroopUnit conquerer, City city) {
        OwnershipHelper.transferCity(city, conquerer.getOwner());
        view.updateCity(city, getPlayerColor(conquerer.getOwner()));
    }
    /**
     * Updates selected entity and UI.
     */
    public void setSelectedEntity(IGridEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
        view.setSelectedEntity(selectedEntity);

        if (selectedEntity instanceof TroopUnit troopUnit) {
            Set<Tile> availableTilesForMovement = gameService.getAvailableTilesForMovement(troopUnit.getId());
            view.showSelectedEntityAvailableMoves(availableTilesForMovement);
        }
    }
}
