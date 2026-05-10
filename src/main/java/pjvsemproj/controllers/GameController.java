package pjvsemproj.controllers;

import javafx.application.Platform;
import pjvsemproj.config.GameConfigParser;
import pjvsemproj.dto.*;
import pjvsemproj.models.services.CoreGameService;
import pjvsemproj.models.services.ClientGameEngine;
import pjvsemproj.views.game.GameView;

import java.util.Objects;
import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

// TODO add winning screen
// TODO on escape pressed: cancel selection or pop up dialog window
/**
 * Main controller connecting UI with game logic.
 */
public class GameController {

    private final GameView view;
    private final ClientGameEngine gameService;
    private final SceneDirector sceneDirector;

    private String selectedEntityId;

    public GameController(ClientGameEngine gameService, GameView view, SceneDirector sceneDirector) {
        this.gameService = gameService;
        this.view = view;
        this.sceneDirector = sceneDirector;

        view.setOnGameAreaClickedAction(this::handleGameAreaClick);
        view.setOnEntitySelectedAction(entity -> setSelectedEntityId(entity.id));

        view.setOnSaveGameAction(this::handleSaveGameRequest);
        view.setOnQuitGameAction(this::handleQuitGameRequest);

        view.setOnBuyUnitAction(this::handleBuyUnit);
        view.setOnUpgradeCityAction(this::handleUpgradeCity);
    }

    public void initialize() {
        gameService.setOnBoardUpdated(() -> {
            // We MUST use Platform.runLater because the Service might
            // trigger this from a background thread
            Platform.runLater(() -> {
                view.setNextTurnButtonDisabled(!gameService.isMyTurn());
                view.redrawMap(gameService.getGameDTO());
                view.updatePlayersBalance(gameService.getPlayersDTO());
                view.updateCurrentPlayer(gameService.getCurrentPlayerDTO().name);

                // Lock or unlock the button depending on if it's our turn now
                setSelectedEntityId(selectedEntityId);
            });
        });

        gameService.setOnGameOver(winnerName -> {
            Platform.runLater(() -> {
                // TODO Show winning screen popup here
                sceneDirector.showGameOverPopup(winnerName);
                System.out.println("GAME OVER! The winner is: " + winnerName);
            });
        });

        view.setOnNextTurnAction(gameService::endTurn);
    }

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

    private void handleEmptyTileClick(int x, int y) {
        if (selectedEntityId == null) return;

        EntityDTO selectedEntity = gameService.getEntityDTO(selectedEntityId);
        if (selectedEntity instanceof TroopUnitDTO) {
            moveTroop(selectedEntityId, x, y);
        }
        setSelectedEntityId(null);
    }

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

    private void handleBuyUnit(String cityId, String troopType) {
        gameService.buyUnit(cityId, troopType);
        EntityDTO city = gameService.getEntityDTO(cityId);
        updateTile(city.x, city.y);

        setSelectedEntityId(cityId);
    }

    private void handleUpgradeCity(String cityId) {
        gameService.upgradeCity(cityId);

        setSelectedEntityId(cityId);
    }

    public void handleSaveGameRequest() {
        sceneDirector.showSaveFileDialog(filePath -> {
            if (filePath != null) {
                try {
                    GameDTO saveState = gameService.getGameDTO();
                    GameConfigParser parser = new GameConfigParser();
                    parser.saveLevelConfig(saveState, filePath);
                    System.out.println("Game saved successfully to: " + filePath);
                } catch (Exception e) {
                    System.err.println("Failed to save game: " + e.getMessage());
                }
            }
        });
    }

    public void handleQuitGameRequest() {
        sceneDirector.showMainMenu();
    }

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

    private void moveTroop(String troopUnitId, int x, int y) {
        TroopUnitDTO troopUnit = (TroopUnitDTO) gameService.getEntityDTO(troopUnitId);
        int oldX = troopUnit.x;
        int oldY = troopUnit.y;
        gameService.moveUnit(troopUnitId, x, y);
        updateTile(oldX, oldY);
        updateTile(x, y);
        setSelectedEntityId(null);
    }

    private void attackTroop(String attackerId, String targetId) {
        TroopUnitDTO target = (TroopUnitDTO) gameService.getEntityDTO(targetId);
        gameService.attack(attackerId, targetId);
        updateTile(target.x, target.y);
        setSelectedEntityId(null);
    }

    public void updateTile(int x, int y) {
        TileDTO tile = gameService.getTileDTO(x, y);
        view.updateTile(tile);
    }

    public GameView getView() {
        return view;
    }

    public CoreGameService getGameService() {
        return gameService;
    }

    public String getSelectedEntityId() {
        return selectedEntityId;
    }
}