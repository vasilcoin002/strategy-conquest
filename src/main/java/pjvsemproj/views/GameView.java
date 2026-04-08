package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    private final GraphicsContext staticEntitiesGc;
    private final GraphicsContext dynamicEntitiesGc;
    private final GraphicsContext overlaysGc;

    private final MapRenderer mapRenderer;

    // TODO transfer game to controller
    private final Game game;
    private IGridEntity selectedEntity;

    public GameView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;

        gameAreaWidth = game.getMap().getWidth() * TILE_SIZE;
        gameAreaHeight = game.getMap().getHeight() * TILE_SIZE;
        Canvas staticEntitesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas dynamicEntitesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas overlaysCanvas = new Canvas(gameAreaWidth, gameAreaHeight);

        staticEntitiesGc = staticEntitesCanvas.getGraphicsContext2D();
        dynamicEntitiesGc = dynamicEntitesCanvas.getGraphicsContext2D();
        overlaysGc = overlaysCanvas.getGraphicsContext2D();

        Pane root = new StackPane(
                staticEntitesCanvas, dynamicEntitesCanvas, overlaysCanvas
        );
        setBackground(root);
        scene = new Scene(root, gameAreaWidth, gameAreaHeight);

        mapRenderer = new MapRenderer();
//        // TODO remove later
        List<Player> players = game.getPlayers();
        for (Player player : players) {
            Color ownerColor = getPlayerColor(game, player);
            List<City> cities = player.getCities();
            List<TroopUnit> troops = player.getTroops();

            mapRenderer.renderCities(staticEntitiesGc, cities, ownerColor);
            mapRenderer.renderTroops(dynamicEntitiesGc, troops, ownerColor);
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

        if (selectedEntity == null) mapRenderer.clear(overlaysGc);
        else mapRenderer.renderSelection(overlaysGc, selectedEntity);
    }

    public void showSelectedEntityAvailableMoves(Set<Tile> availableTiles) {
        mapRenderer.renderAvailableMoves(overlaysGc ,availableTiles);
    }
}
