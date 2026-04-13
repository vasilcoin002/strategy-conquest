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
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.views.renderers.*;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    private IGridEntity selectedEntity;

    private BiConsumer<Integer, Integer> onGameAreaClickedAction;

    public GameView(Stage stage, int mapModelWidth, int mapModelHeight,
            List<Player> players, Player currentPlayer, Color player1Color, Color player2Color) {
        this.stage = stage;

        gameAreaWidth = mapModelWidth * TILE_SIZE;
        gameAreaHeight = mapModelHeight * TILE_SIZE;
        Canvas staticEntitesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas dynamicEntitesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas overlaysCanvas = new Canvas(gameAreaWidth, gameAreaHeight);

        overlaysCanvas.setOnMouseClicked(e -> {
            onGameAreaClickedAction.accept((int)e.getX(), (int)e.getY());
        });

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

        Color ownerColor = player1Color;
        for (Player player : players) {
            List<City> cities = player.getCities();
            List<TroopUnit> troops = player.getTroops();

            mapRenderer.renderCities(staticEntitiesGc, cities, ownerColor);
            mapRenderer.renderTroops(dynamicEntitiesGc, troops, ownerColor);
            ownerColor = player2Color;
        }

        sidePanel.updatePlayersBalance(players);
        sidePanel.updateCurrentPlayer(currentPlayer);
    }

    public void show() {
        stage.setScene(scene);
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

        mapRenderer.clear(overlaysGc);
        sidePanel.clearEntityInfo();
        sidePanel.updateForTile(null);

        if (selectedEntity != null) {
            mapRenderer.renderSelection(overlaysGc, selectedEntity);
            sidePanel.updateEntityInfo(selectedEntity);
            sidePanel.updateForTile(selectedEntity.getTile());
        }
    }

    public void showSelectedEntityAvailableMoves(Set<Tile> availableTiles) {
        mapRenderer.renderAvailableMoves(overlaysGc ,availableTiles);
    }

    public void updateTroopUnit(TroopUnit troopUnit, Color ownerColor) {
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
                    ownerColor
            );
        }
    }

    public void clearTroopUnit(TroopUnit troopUnit) {
        mapRenderer.clearTroopUnit(dynamicEntitiesGc, troopUnit);
    }

    public void updateCity(City city, Color ownerColor) {
        mapRenderer.renderCity(staticEntitiesGc, city, ownerColor);
    }

    public void setOnGameAreaClickedAction(BiConsumer<Integer, Integer> onGameAreaClickedAction) {
        this.onGameAreaClickedAction = onGameAreaClickedAction;
    }

    public void setOnEntitySelectedAction(Consumer<IGridEntity> onEntitySelectedAction) {
        sidePanel.setOnEntitySelectedAction(onEntitySelectedAction);
    }

    public void setOnQuitGameAction(Runnable onQuitGameAction) {
        sidePanel.setOnQuitGameAction(onQuitGameAction);
    }

    public void setOnNextTurnAction(Runnable onNextTurnAction) {
        sidePanel.setOnNextTurnAction(onNextTurnAction);
    }

    public void setNextTurnButtonDisabled(boolean disabled) {
        sidePanel.setNextTurnButtonDisabled(disabled);
    }
}
