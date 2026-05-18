package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.config.GameConfigParser;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.TileDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.services.ClientGameEngine;
import pjvsemproj.models.services.NetworkGameService;
import pjvsemproj.views.game.GameView;

import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Main application coordinator implementing the Controller role in MVC patterns.
 * <p>
 * Mediates communication and sync between the presentation view graph layer ({@link GameView})
 * and the client simulation engine logic ({@link ClientGameEngine}). Handles inputs including selection,
 * movement calculations, troop production actions, and state transitions on JavaFX UI threads.
 */
public class GameController {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private final GameView view;
    private final ClientGameEngine gameService;
    private final SceneDirector sceneDirector;

    private String selectedEntityId;

    /**
     * Constructs a controller instance and binds input actions from the user interface.
     * @param gameService   The client-side game engine service handling operational rules and simulation state.
     * @param view          The JavaFX graphical user interface wrapper displaying game assets and grid elements.
     * @param sceneDirector The central manager orchestrating scene transformations and application display states.
     */
    public GameController(ClientGameEngine gameService, GameView view, SceneDirector sceneDirector) {
        this.gameService = gameService;
        this.view = view;
        this.sceneDirector = sceneDirector;

        view.setOnGameAreaClickedAction(this::handleGameAreaClick);
        view.setOnEntitySelectedAction(entity -> setSelectedEntityId(entity.id));
        view.setOnEscapeAction(() -> {
            if (selectedEntityId != null) {
                setSelectedEntityId(null);
            }
        });

        view.setOnSaveGameAction(this::handleSaveGameRequest);
        view.setOnQuitGameAction(this::handleQuitGameRequest);

        view.setOnBuyUnitAction(this::handleBuyUnit);
        view.setOnUpgradeCityAction(this::handleUpgradeCity);
    }

    /**
     * Initializes the controller lifecycle hooks and connects asynchronous backend engine state listeners.
     * <p>
     * Leverages {@link Platform#runLater(Runnable)} to guarantee thread-safe rendering on the JavaFX main thread
     * whenever game updates arrive from background network listeners.
     */
    public void initialize() {
        // We must use Platform.runLater because the Service might
        // trigger this from a background thread
        Platform.runLater(this::syncGameStateToUI);

        gameService.setOnBoardUpdated(() -> Platform.runLater(this::syncGameStateToUI));

        gameService.setOnGameOver(winnerName -> {

            // 1. If this is a network game, tell the server to shut down safely
            if (gameService instanceof NetworkGameService networkService) {
                networkService.notifyServerOfWin(winnerName);
            }

            Platform.runLater(() -> sceneDirector.showGameOverPopup(winnerName));
        });

        view.setOnNextTurnAction(gameService::endTurn);

        gameService.ready();
    }

    /**
     * Centralizes UI updates to ensure the view perfectly reflects the current GameState.
     * <p>
     * Re-draws the map grids, syncs participant bank accounts, re-evaluates turn label values,
     * and dynamically adjusts selection action panels based on ownership permissions.
     */
    private void syncGameStateToUI() {
        view.setNextTurnButtonDisabled(!gameService.isMyTurn());
        view.redrawMap(gameService.getGameDTO());
        view.updatePlayersBalance(gameService.getPlayersDTO());
        view.updateCurrentPlayer(gameService.getCurrentPlayerDTO().name);

        // Lock or unlock the selected entity depending on whose turn it is
        setSelectedEntityId(selectedEntityId);
    }

    /**
     * Translates screen-space pixel click inputs into operational grid tile indexes.
     * <p>
     * Evaluates if a click hit an empty square or an occupied coordinate block to delegate
     * contextual actions appropriately.
     *
     * @param viewX Click position along the horizontal component axis in pixels.
     * @param viewY Click position along the vertical component axis in pixels.
     */
    private void handleGameAreaClick(int viewX, int viewY) {
        int x = viewX / TILE_SIZE;
        int y = viewY / TILE_SIZE;
        TileDTO tile = gameService.getTileDTO(x, y);

        // Handle empty tile
        if (tile.entities.isEmpty()) {
            handleEmptyTileClick(x, y);
            return;
        }

        handleOccupiedTileClick(tile);
    }

    /**
     * Processes execution scopes when an unallocated tile destination is selected.
     * <p>
     * If an applicable unit is currently locked onto selection, it dispatches a relocation move sequence.
     *
     * @param x Target tile horizontal column grid index.
     * @param y Target tile vertical row grid index.
     */
    private void handleEmptyTileClick(int x, int y) {
        if (selectedEntityId == null) return;

        EntityDTO selectedEntity = gameService.getEntityDTO(selectedEntityId);
        if (selectedEntity instanceof TroopUnitDTO) {
            moveTroop(selectedEntityId, x, y);
        }
        setSelectedEntityId(null);
    }

    /**
     * Evaluates selection changes or interaction behaviors when an occupied grid node is targeted.
     * <p>
     * Resolves complex contextual logic branching such as dropping selection if the unit is re-clicked,
     * recalculating target layers, executing unit re-selections, or parsing attack action triggers.
     *
     * @param targetTile The structural grid cell containing data representation properties to interact with.
     */
    private void handleOccupiedTileClick(TileDTO targetTile) {
        // if entity is not selected yet => select the very top entity on tile
        if (selectedEntityId == null) {
            setSelectedEntityId(targetTile.entities.getLast().id);
            return;
        }

        // if it's the same entity => unselect it
        for (EntityDTO entity: targetTile.entities) {
            if (Objects.equals(entity.id, selectedEntityId)) {
                setSelectedEntityId(null);
                return;
            }
        }

        EntityDTO currentEntity = gameService.getEntityDTO(selectedEntityId);
        EntityDTO targetEntity = targetTile.entities.getLast();
        if (currentEntity instanceof TroopUnitDTO currentTroopUnit) {
            Set<TileDTO> tilesToMove = gameService.getAvailableTilesDTOForMovement(currentTroopUnit.id);
            Set<TileDTO> tilesToAttack = gameService.getAvailableTilesDTOForAttack(currentTroopUnit.id);

            // if troop can't move on or attack target tile
            if (!tilesToMove.contains(targetTile) && !tilesToAttack.contains(targetTile)) {
                setSelectedEntityId(targetTile.entities.getLast().id);
                return;
            }
            // if troop can move on target tile
            if (tilesToMove.contains(targetTile)) {
                moveTroop(selectedEntityId, targetTile.x, targetTile.y);
                return;
            }
            // if troop can attack target tile
            if (tilesToAttack.contains(targetTile)
                    && targetEntity instanceof TroopUnitDTO targetTroopUnit) {
                attackTroop(currentTroopUnit.id, targetTroopUnit.id);
                return;
            }
        }

        setSelectedEntityId(targetEntity.id);
    }

    /**
     * Dispatches unit production requests to the logic engine and triggers local grid refreshes.
     *
     * @param cityId    Unique string token identifier of the factory settlement creating the asset.
     * @param troopType Structural metadata enum descriptor representing the chosen military class configuration.
     */
    private void handleBuyUnit(String cityId, String troopType) {
        gameService.buyUnit(cityId, troopType);
        EntityDTO city = gameService.getEntityDTO(cityId);
        updateTile(city.x, city.y);

        setSelectedEntityId(cityId);
    }

    /**
     * Dispatches tier transformation commands to upgrade a target city structure.
     *
     * @param cityId Unique metadata token string identifying the settlement to upgrade.
     */
    private void handleUpgradeCity(String cityId) {
        gameService.upgradeCity(cityId);

        setSelectedEntityId(cityId);
    }

    /**
     * Intercepts file export triggers to serialize the operational game data down onto disk storage.
     * <p>
     * Utilizes a file picker popup to extract a destination path before converting DTO structures into JSON format.
     */
    public void handleSaveGameRequest() {
        sceneDirector.showSaveFileDialog(filePath -> {
            if (filePath != null) {
                try {
                    GameDTO saveState = gameService.getGameDTO();
                    GameConfigParser parser = new GameConfigParser();
                    parser.saveLevelConfig(saveState, filePath);
                    LOGGER.info("Game saved successfully to: " + filePath);
                } catch (Exception e) {
                    LOGGER.severe("Failed to save game: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Processes quit triggers by closing network sockets or teardown local dependencies,
     * before redirecting the interface back to the main menu screen.
     */
    public void handleQuitGameRequest() {
        gameService.quit();
        sceneDirector.showMainMenu();
    }

    /**
     * Updates the internal active targeting tracker and refreshes selection overlay highlights.
     * <p>
     * Evaluates ownership criteria to decide if user control panels should be displayed,
     * and handles highlighting overlay grids for unit actions.
     *
     * @param entityId The target unique identification token to isolate, or {@code null} to drop active targeting tracking.
     */
    public void setSelectedEntityId(String entityId) {
        this.selectedEntityId = entityId;
        EntityDTO entity = gameService.getEntityDTO(entityId);

        boolean isOwner = false;
        if (entity != null) {
            isOwner = Objects.equals(entity.ownerName, gameService.getClientName());
        }

        view.setSelectedEntity(entity, isOwner);

        TileDTO tile = null;
        if (entity != null) {
            tile = gameService.getTileDTO(entity.x, entity.y);
        }
        view.updateTileEntitiesInfo(tile);

        if (entity instanceof TroopUnitDTO) {
            Set<TileDTO> tilesToMove = gameService.getAvailableTilesDTOForMovement(entityId);
            Set<TileDTO> tilesToAttack = gameService.getAvailableTilesDTOForAttack(entityId);
            view.showSelectedEntityAvailableMoves(tilesToMove);
            view.showSelectedEntityAvailableAttacks(tilesToAttack);
        }
    }

    /**
     * Executes coordinates translation pipelines to adjust position states across components.
     * <p>
     * Clears original cell coordinates and forces redraws over both origin and target boundaries.
     *
     * @param troopUnitId Unique lookup key identifying the moving entity.
     * @param x           Target horizontal destination column index.
     * @param y           Target vertical destination row index.
     */
    private void moveTroop(String troopUnitId, int x, int y) {
        TroopUnitDTO troopUnit = (TroopUnitDTO) gameService.getEntityDTO(troopUnitId);
        int oldX = troopUnit.x;
        int oldY = troopUnit.y;
        gameService.moveUnit(troopUnitId, x, y);
        updateTile(oldX, oldY);
        updateTile(x, y);
        setSelectedEntityId(null);
    }

    /**
     * Executes interaction loops between attacking assets and matching targeted unit positions.
     * <p>
     * Triggers the combat calculations and re-renders modified grid boundaries.
     *
     * @param attackerId Unique verification token matching the attacking unit.
     * @param targetId   Unique verification token matching the defending unit destination.
     */
    private void attackTroop(String attackerId, String targetId) {
        TroopUnitDTO target = (TroopUnitDTO) gameService.getEntityDTO(targetId);
        gameService.attack(attackerId, targetId);
        updateTile(target.x, target.y);
        setSelectedEntityId(null);
    }

    /**
     * Forces visual synchronization of specific layout grids within viewport boundaries.
     *
     * @param x Target tile horizontal cell coordinate.
     * @param y Target tile vertical cell coordinate.
     */
    public void updateTile(int x, int y) {
        TileDTO tile = gameService.getTileDTO(x, y);
        view.updateTile(tile);
    }
}