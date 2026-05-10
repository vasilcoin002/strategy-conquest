package pjvsemproj.views.game;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pjvsemproj.dto.*;
import pjvsemproj.views.game.renderers.MapRenderer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static pjvsemproj.views.ViewConstants.GAME_SIDE_PANEL_WIDTH;
import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Main game UI class.
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

    private Runnable onEscapeAction;
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

        entitiesGc = entitiesCanvas.getGraphicsContext2D();
        overlaysGc = overlaysCanvas.getGraphicsContext2D();

        StackPane mapPane = new StackPane(entitiesCanvas, overlaysCanvas);
        setBackground(mapPane);

        sidePanel = new SidePanelView();

        root = new BorderPane();
        root.setCenter(mapPane);
        root.setRight(sidePanel.getView());

        overlaysCanvas.setOnMouseClicked(e -> {
            if(onGameAreaClickedAction != null) {
                onGameAreaClickedAction.accept((int)e.getX(), (int)e.getY());
            }
            // steal focus back when clicking the map!
            root.requestFocus();
        });

        setBackground(mapPane);
        scene = new Scene(root, gameAreaWidth + GAME_SIDE_PANEL_WIDTH, gameAreaHeight);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (onEscapeAction != null) onEscapeAction.run();
            }
        });

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
        sidePanel.updateCurrentPlayer(game.currentPlayerName);

        // allow the main game root to hold focus
        root.setFocusTraversable(true);
        // steal the focus immediately after the scene renders
        Platform.runLater(root::requestFocus);
    }

    public void show(Stage stage, String clientName) {
        stage.setTitle("Strategy Game - " + clientName);
        stage.setScene(scene);
    }

    public void redrawMap(GameDTO game) {
        mapRenderer.clear(entitiesGc);

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
    }

    public void updatePlayersBalance(List<PlayerDTO> players) {
        sidePanel.updatePlayersBalance(players);
    }

    public void updateCurrentPlayer(String currentPlayerName) {
        sidePanel.updateCurrentPlayer(currentPlayerName);
    }

    public void updateTileEntitiesInfo(TileDTO tile) {
        sidePanel.updateForTile(tile);
    }

    public Background getBackground() {
        Image grassTexture = new Image("grass.png");

        BackgroundImage backgroundImage = new BackgroundImage(
                grassTexture,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );

        return new Background(backgroundImage);
    }

    public void setBackground(Pane pane) {
        pane.setBackground(getBackground());
    }

    public EntityDTO getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(EntityDTO selectedEntity, boolean isOwner) {
        this.selectedEntity = selectedEntity;

        mapRenderer.clear(overlaysGc);
        sidePanel.clearEntityInfo();
        sidePanel.updateForTile(null);

        if (selectedEntity != null) {
            mapRenderer.renderSelection(overlaysGc, selectedEntity);
            sidePanel.updateEntityInfo(selectedEntity, isOwner);
        }

        // steal the focus back to the main container
        root.requestFocus();
    }

    public void showSelectedEntityAvailableMoves(Set<TileDTO> tilesToMove) {
        mapRenderer.renderAvailableMoves(overlaysGc, tilesToMove);
    }

    public void showSelectedEntityAvailableAttacks(Set<TileDTO> tilesToAttack) {
        mapRenderer.renderAvailableAttacks(overlaysGc, tilesToAttack);
    }

    public void updateTile(TileDTO tile) {
        mapRenderer.clearTile(entitiesGc, tile);
        mapRenderer.renderTile(entitiesGc, tile, ownersColors);
    }

    public void setOnEscapeAction(Runnable onEscapeAction) {
        this.onEscapeAction = onEscapeAction;
    }

    public void setOnGameAreaClickedAction(BiConsumer<Integer, Integer> onGameAreaClickedAction) {
        this.onGameAreaClickedAction = onGameAreaClickedAction;
    }

    public void setOnEntitySelectedAction(Consumer<EntityDTO> onEntitySelectedAction) {
        sidePanel.setOnEntitySelectedAction(onEntitySelectedAction);
    }

    public void setOnBuyUnitAction(BiConsumer<String, String> onBuyUnitAction) {
        sidePanel.setOnBuyUnitAction(onBuyUnitAction);
    }

    public void setOnUpgradeCityAction(Consumer<String> onUpgradeCityAction) {
        sidePanel.setOnUpgradeCityAction(onUpgradeCityAction);
    }

    public void setOnQuitGameAction(Runnable onQuitGameAction) {
        sidePanel.setOnQuitGameAction(onQuitGameAction);
    }

    public void setOnSaveGameAction(Runnable onSaveGameAction) {
        sidePanel.setOnSaveGameAction(onSaveGameAction);
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