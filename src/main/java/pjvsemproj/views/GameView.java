package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.renderers.*;

import java.util.List;
import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public class GameView {

    private final int gameAreaWidth;
    private final int gameAreaHeight;

    private final Stage stage;
    private final Scene scene;
    // TODO transfer game to controller
    private final Game game;
    private IGridEntity selectedEntity;

    private final CitiesRenderer citiesRenderer;
    private final TroopsRenderer troopsRenderer;
    private final OwnershipRenderer ownershipRenderer;
    private final HpRenderer hpRenderer;
    private final SelectionRenderer selectionRenderer;
    private final MovementRenderer movementRenderer;

    public GameView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;

        gameAreaWidth = game.getMap().getWidth() * TILE_SIZE;
        gameAreaHeight = game.getMap().getHeight() * TILE_SIZE;
        Canvas citiesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas troopsCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas ownershipCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas hpCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas selectionCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas movementCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Pane root = new StackPane(
                citiesCanvas, troopsCanvas, ownershipCanvas,
                hpCanvas, selectionCanvas, movementCanvas
        );
        setBackground(root);
        scene = new Scene(root, gameAreaWidth, gameAreaHeight);

        citiesRenderer = new CitiesRenderer(citiesCanvas);
        troopsRenderer = new TroopsRenderer(troopsCanvas);
        ownershipRenderer = new OwnershipRenderer(ownershipCanvas);
        hpRenderer = new HpRenderer(hpCanvas);
        selectionRenderer = new SelectionRenderer(selectionCanvas);
        movementRenderer = new MovementRenderer(movementCanvas);
//        // TODO remove later
        List<Player> players = game.getPlayers();
        for (Player player : players) {
            Color color = getPlayerColor(game, player);
            List<City> cities = player.getCities();
            List<TroopUnit> troops = player.getTroops();
            citiesRenderer.renderCities(cities);
            troopsRenderer.renderTroops(troops);
            ownershipRenderer.renderEntitiesOwner(cities, color);
            ownershipRenderer.renderEntitiesOwner(troops, color);
            hpRenderer.renderEntitiesHp(troops);
        }

        setSelectedEntity(players.getLast().getTroops().getFirst());
    }

    public void show() {
        stage.setScene(scene);
    }

    public Color getPlayerColor(Game game, Player player) {
        if (game.getPlayers().getFirst() == player) return Color.BLUE;
        return Color.ORANGE;
    }

    public Background getBackground() {
        Image grassTexture = new Image("grass.png");

        BackgroundImage backgroundImage = new BackgroundImage(
                grassTexture,
                BackgroundRepeat.REPEAT,   // horizontally
                BackgroundRepeat.REPEAT,   // vertically
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT     // keep the exact 64x64 pixel size (don't stretch)
        );

        return new Background(backgroundImage);
    }

    public void setBackground(Pane pane) {
        pane.setBackground(getBackground());
    }

    public IGridEntity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(IGridEntity selectedEntity) {
        this.selectedEntity = selectedEntity;

        if (selectedEntity == null) selectionRenderer.clear();
        else selectionRenderer.renderSelection(selectedEntity);
    }

    public void showSelectedEntityAvailableMoves(Set<Tile> availableTiles) {
        movementRenderer.renderAvailableTiles(availableTiles);
    }
}
