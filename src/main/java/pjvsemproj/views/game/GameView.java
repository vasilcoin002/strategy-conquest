package pjvsemproj.views.game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.dto.*;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.views.game.renderers.MapRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;
import static pjvsemproj.views.ViewConstants.TILE_SIZE;

// TODO fix side menu doesn't show two entities on one tile
// TODO fix changing selection from entity to entity works incorrectly
//  (now it works only for entity-empty-entity, but doesn't work for entity-entity)
/**
 * Main game UI class.
 *
 * Responsible for rendering the map, entities, and handling user interaction.
 */
public class GameView {

    private final int gameAreaWidth;
    private final int gameAreaHeight;
    private final Scene scene;
    private final BorderPane root;

    private Map<String, Color> ownersColors;

    private final GraphicsContext entitiesGc;
    private final GraphicsContext overlaysGc;
    private final MapRenderer mapRenderer;

    private final SidePanelView sidePanel;

    private EntityDTO selectedEntity;

    private BiConsumer<Integer, Integer> onGameAreaClickedAction;

    public GameView(
            GameDTO game,
            Map<String, Color> ownersColors
    ) {
        gameAreaWidth = game.mapWidth * TILE_SIZE;
        gameAreaHeight = game.mapHeight * TILE_SIZE;
        this.ownersColors = ownersColors;

        Canvas entitiesCanvas = new Canvas(gameAreaWidth, gameAreaHeight);
        Canvas overlaysCanvas = new Canvas(gameAreaWidth, gameAreaHeight);

        overlaysCanvas.setOnMouseClicked(e -> {
            onGameAreaClickedAction.accept((int)e.getX(), (int)e.getY());
        });

        entitiesGc = entitiesCanvas.getGraphicsContext2D();
        overlaysGc = overlaysCanvas.getGraphicsContext2D();

        StackPane mapPane = new StackPane(entitiesCanvas, overlaysCanvas);
        setBackground(mapPane);

        sidePanel = new SidePanelView();

        root = new BorderPane();
        root.setCenter(mapPane);
        root.setRight(sidePanel.getView());

        setBackground(mapPane);
        scene = new Scene(root, gameAreaWidth + GAME_SIDE_PANEL_WIDTH, gameAreaHeight);

        mapRenderer = new MapRenderer();

        List<CityDTO> cities = new ArrayList<>();
        List<TroopUnitDTO> troops = new ArrayList<>();
        for (EntityDTO entity : game.entities) {
            if (entity instanceof CityDTO city) {
                cities.add(city);
            } else if (entity instanceof TroopUnitDTO troopUnit) {
                troops.add(troopUnit);
            }
        }

        mapRenderer.renderCities(entitiesGc, cities, ownersColors);
        mapRenderer.renderTroops(entitiesGc, troops, ownersColors);

        sidePanel.updatePlayersBalance(game.players);
        sidePanel.updateCurrentPlayer(game.currentPlayer);
    }

    public void show(Stage stage) {
        stage.setScene(scene);
    }

    public void updatePlayersBalance(List<PlayerDTO> players) {
        sidePanel.updatePlayersBalance(players);
    }

    public void updateCurrentPlayer(PlayerDTO currentPlayer) {
        sidePanel.updateCurrentPlayer(currentPlayer);
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

    public EntityDTO getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(EntityDTO selectedEntity) {
        this.selectedEntity = selectedEntity;

        mapRenderer.clear(overlaysGc);
        sidePanel.clearEntityInfo();
        sidePanel.updateForTile(null);

        if (selectedEntity != null) {
            mapRenderer.renderSelection(overlaysGc, selectedEntity);
            sidePanel.updateEntityInfo(selectedEntity);
            // TODO updateForTile
//            sidePanel.updateForTile(selectedEntity.getTile());
        }
    }

    public void showSelectedEntityAvailableMoves(Set<TileDTO> tilesToMove) {
        mapRenderer.renderAvailableMoves(overlaysGc, tilesToMove);
    }

    // TODO implement
    public void showSelectedEntityAvailableAttacks(Set<Tile> tilesToAttack) {

    }

    public void updateTile(TileDTO tile) {
        mapRenderer.clearTile(entitiesGc, tile);
        mapRenderer.renderTile(entitiesGc, tile, ownersColors);
    }

//    public void updateTroopUnit(TroopUnitDTO troopUnit, Color ownerColor) {
//        int hp = troopUnit.hp;
//
//        if (hp <= 0) {
//            mapRenderer.clearTroopUnit(entitiesGc ,troopUnit);
//            if (selectedEntity == troopUnit) {
//                setSelectedEntity(null);
//            }
//        } else {
//            mapRenderer.renderTroop(
//                    entitiesGc,
//                    troopUnit,
//                    ownerColor
//            );
//        }
//    }

//    public void clearTroopUnit(TroopUnit troopUnit) {
//        mapRenderer.clearTroopUnit(dynamicEntitiesGc, troopUnit);
//    }

//    public void updateCity(City city, Color ownerColor) {
//        mapRenderer.renderCity(staticEntitiesGc, city, ownerColor);
//    }

    public void setOnGameAreaClickedAction(BiConsumer<Integer, Integer> onGameAreaClickedAction) {
        this.onGameAreaClickedAction = onGameAreaClickedAction;
    }

    public void setOnEntitySelectedAction(Consumer<EntityDTO> onEntitySelectedAction) {
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

    public BorderPane getRoot() {
        return root;
    }
}
