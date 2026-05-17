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

// TODO make this non scalable
/**
 * Main game UI configuration management screen layer.
 * <p>
 * Responsible for rendering the map, entities, and handling user interaction.
 * It coordinates stacked rendering layers (underlying sprites canvas and overlay highlight grids canvas),
 * sets up tile textures, captures input keys, and routes panel action menus onto engine wrappers.
 */
public class GameView {

    private final Scene scene;
    private final BorderPane root;

    private final Map<String, Color> ownersColors;

    private final GraphicsContext entitiesGc;
    private final GraphicsContext overlaysGc;
    private final MapRenderer mapRenderer;

    private final SidePanelView sidePanel;

    private Runnable onEscapeAction;
    private BiConsumer<Integer, Integer> onGameAreaClickedAction;

    /**
     * Constructs the primary game viewport grid and binds mouse interaction triggers.
     * <p>
     * Allocates standard system canvas layers, sets default pixel dimensions, processes
     * map entities for initial rendering loops, and anchors focus-traversal criteria.
     *
     * @param game         The initialized {@link GameDTO} state container model containing match data properties.
     * @param ownersColors A map pairing participant identity names onto distinct JavaFX color values.
     */
    public GameView(
            GameDTO game,
            Map<String, Color> ownersColors
    ) {
        int gameAreaWidth = game.mapWidth * TILE_SIZE;
        int gameAreaHeight = game.mapHeight * TILE_SIZE;
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

    /**
     * Binds the fully generated scene wrapper onto the application stage display container.
     *
     * @param stage      The primary structural execution window managed by the runtime environment.
     * @param clientName The identity profile name string utilized to configure the title banner.
     */
    public void show(Stage stage, String clientName) {
        stage.setTitle("Strategy Game - " + clientName);
        stage.setScene(scene);
    }

    /**
     * Completely erases the active element grid canvas layer and performs a full map redraw loop.
     *
     * @param game The fresh master {@link GameDTO} container model snapshot to draw.
     */
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

    /**
     * Updates economic balance labels inside control sections.
     *
     * @param players An itemized data checklist collection of participating player models.
     */
    public void updatePlayersBalance(List<PlayerDTO> players) {
        sidePanel.updatePlayersBalance(players);
    }

    /**
     * Updates the text display tracking who holds turn action clearances.
     *
     * @param currentPlayerName Profile username string tracking the active player.
     */
    public void updateCurrentPlayer(String currentPlayerName) {
        sidePanel.updateCurrentPlayer(currentPlayerName);
    }

    /**
     * Refreshes contextual item switching buttons when a map cell is queried.
     *
     * @param tile The structural map grid cell {@link TileDTO} container being inspected.
     */
    public void updateTileEntitiesInfo(TileDTO tile) {
        sidePanel.updateForTile(tile);
    }

    /**
     * Compiles a standard background layout configuration matching grid system requirements.
     * <p>
     * Loads a texture file and builds an entry to handle tiling repeating boundaries.
     *
     * @return A completed {@link Background} theme container ready for attachment.
     */
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

    /**
     * Sets the textured graphic layout background over target display canvas panes.
     *
     * @param pane The target layout node component container to modify.
     */
    public void setBackground(Pane pane) {
        pane.setBackground(getBackground());
    }

    /**
     * Clears overlay indicators, calculates control options, and applies selection circles over a target node.
     * <p>
     * Refreshes side panel statistical descriptions and steals screen focus flags back to the core container.
     *
     * @param selectedEntity The targeted entity model to encircle, or {@code null} to clear selection.
     * @param isOwner        Permission flag specifying if the local player controls the asset.
     */
    public void setSelectedEntity(EntityDTO selectedEntity, boolean isOwner) {
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

    /**
     * Renders path move rings over a set checklist of reachable positions.
     *
     * @param tilesToMove A {@link Set} of valid path cells destination tiles.
     */
    public void showSelectedEntityAvailableMoves(Set<TileDTO> tilesToMove) {
        mapRenderer.renderAvailableMoves(overlaysGc, tilesToMove);
    }

    /**
     * Renders attack indicator rings over a set checklist of target enemy coordinates.
     *
     * @param tilesToAttack A {@link Set} of targetable combat cells destination tiles.
     */
    public void showSelectedEntityAvailableAttacks(Set<TileDTO> tilesToAttack) {
        mapRenderer.renderAvailableAttacks(overlaysGc, tilesToAttack);
    }

    /**
     * Directs the renderer to redraw a single specific coordinate layout tile block cleanly.
     *
     * @param tile The specific modified cell {@link TileDTO} container to redraw.
     */
    public void updateTile(TileDTO tile) {
        mapRenderer.clearTile(entitiesGc, tile);
        mapRenderer.renderTile(entitiesGc, tile, ownersColors);
    }

    /**
     * Registers a shortcut listener routine to catch Escape keyboard inputs.
     *
     * @param onEscapeAction A {@link Runnable} closure containing deselect instructions.
     */
    public void setOnEscapeAction(Runnable onEscapeAction) {
        this.onEscapeAction = onEscapeAction;
    }

    /**
     * Registers a callback closure to parse and process mouse click locations on the map grid.
     *
     * @param onGameAreaClickedAction A {@link BiConsumer} tracking horizontal and vertical click coordinate pixels.
     */
    public void setOnGameAreaClickedAction(BiConsumer<Integer, Integer> onGameAreaClickedAction) {
        this.onGameAreaClickedAction = onGameAreaClickedAction;
    }

    /**
     * Registers an intercept closure hook onto side panels to handle item selection swaps.
     *
     * @param onEntitySelectedAction A {@link Consumer} closure handling selection modifications.
     */
    public void setOnEntitySelectedAction(Consumer<EntityDTO> onEntitySelectedAction) {
        sidePanel.setOnEntitySelectedAction(onEntitySelectedAction);
    }

    /**
     * Registers a callback closure hook onto action controllers to process unit purchases.
     *
     * @param onBuyUnitAction A {@link BiConsumer} mapping factory structure locations onto target class tags.
     */
    public void setOnBuyUnitAction(BiConsumer<String, String> onBuyUnitAction) {
        sidePanel.setOnBuyUnitAction(onBuyUnitAction);
    }

    /**
     * Registers a callback closure hook onto action controllers to handle city tier upgrades.
     *
     * @param onUpgradeCityAction A {@link Consumer} closure processing structure tracking tokens.
     */
    public void setOnUpgradeCityAction(Consumer<String> onUpgradeCityAction) {
        sidePanel.setOnUpgradeCityAction(onUpgradeCityAction);
    }

    /**
     * Registers a callback closure hook onto action controllers to handle voluntary match resignations.
     *
     * @param onQuitGameAction A {@link Runnable} closure executing exit sequences.
     */
    public void setOnQuitGameAction(Runnable onQuitGameAction) {
        sidePanel.setOnQuitGameAction(onQuitGameAction);
    }

    /**
     * Registers a callback closure hook onto action controllers to process level file saves.
     *
     * @param onSaveGameAction A {@link Runnable} closure executing export conversions.
     */
    public void setOnSaveGameAction(Runnable onSaveGameAction) {
        sidePanel.setOnSaveGameAction(onSaveGameAction);
    }

    /**
     * Registers a callback closure hook onto action controllers to advance player turns.
     *
     * @param onNextTurnAction A {@link Runnable} closure handling turn changes.
     */
    public void setOnNextTurnAction(Runnable onNextTurnAction) {
        sidePanel.setOnNextTurnAction(onNextTurnAction);
    }

    /**
     * Dynamically switches input capabilities on the turn finalization controls.
     *
     * @param disabled {@code true} to disable the Next Turn button; {@code false} to enable it.
     */
    public void setNextTurnButtonDisabled(boolean disabled) {
        sidePanel.setNextTurnButtonDisabled(disabled);
    }
}