package pjvsemproj.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
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

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;
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

    private final SidePanelView sidePanel;

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

        StackPane mapPane = new StackPane(staticEntitesCanvas, dynamicEntitesCanvas, overlaysCanvas);
        setBackground(mapPane);

        sidePanel = new SidePanelView();

        BorderPane root = new BorderPane();
        root.setCenter(mapPane);
        root.setRight(sidePanel.getView());

        setBackground(mapPane);
        scene = new Scene(root, gameAreaWidth + GAME_SIDE_PANEL_WIDTH, gameAreaHeight);

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

        Player player1 = players.getFirst();
        sidePanel.updatePlayersBalance(players);
        sidePanel.updateCurrentPlayer(player1);
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

        if (selectedEntity == null) {
            mapRenderer.clear(overlaysGc);
            sidePanel.clearEntityInfo();
        }
        else {
            mapRenderer.renderSelection(overlaysGc, selectedEntity);
            sidePanel.updateEntityInfo(selectedEntity);
        }
    }

    public void showSelectedEntityAvailableMoves(Set<Tile> availableTiles) {
        mapRenderer.renderAvailableMoves(overlaysGc ,availableTiles);
    }

    public void updateTroopUnit(TroopUnit troopUnit) {
        int hp = troopUnit.getHealth();

        if (hp <= 0) {
            mapRenderer.clearTroopUnit(dynamicEntitiesGc ,troopUnit);
            if (selectedEntity == troopUnit) {
                setSelectedEntity(null);
            }
        } else {
            mapRenderer.renderTroop(
                    dynamicEntitiesGc,
                    troopUnit,
                    getPlayerColor(game, troopUnit.getOwner())
            );
        }
    }
}
