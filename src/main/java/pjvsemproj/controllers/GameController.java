package pjvsemproj.controllers;

import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.services.GameService;
import pjvsemproj.views.game.GameView;

import java.util.List;
import java.util.Set;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Main controller connecting UI with game logic.
 */
public class GameController {

    private final GameView view;
    private final GameService gameService;

    // 1. Store the ID of the entity, not the raw IGridEntity object
    private String selectedEntityId;

    public GameController(GameService gameService, GameView view) {
        this.gameService = gameService;
        this.view = view;

        view.setOnGameAreaClickedAction(this::handleGameAreaClick);

        view.setOnNextTurnAction(() -> {
            gameService.endTurn();
            // 2. Use DTO methods
            view.updatePlayersBalance(gameService.getPlayersDTO());
            view.updateCurrentPlayer(gameService.getCurrentPlayerDTO());
            setSelectedEntityId(selectedEntityId);
        });
    }

//    public Color getPlayerColor(PlayerDTO player) {
//        // Assume PlayerDTO has a getter for the name or ID to check who is player 1
//        if (gameService.getPlayersDTO().getFirst().name.equals(player.name)) return Color.BLUE;
//        else return Color.ORANGE;
//    }

    private void handleGameAreaClick(int viewX, int viewY) {
        int x = viewX / TILE_SIZE;
        int y = viewY / TILE_SIZE;

        TileDTO tile = gameService.getTileDTO(x, y);
        List<EntityDTO> entitiesOnTile = tile.entities;

        if (entitiesOnTile.isEmpty()) {
            if (selectedEntityId != null) {
                // 4. Delegate all movement to the service
                gameService.moveUnit(selectedEntityId, x, y);
                // Note: The UI redraw should happen via the Service listeners discussed previously
            }
            setSelectedEntityId(null);
        } else {
            EntityDTO targetEntity = entitiesOnTile.getLast();

            if (selectedEntityId != null && !selectedEntityId.equals(targetEntity.id)) {
                // 5. Delegate attacking/conquering to the service
                gameService.attack(selectedEntityId, targetEntity.id);
            } else {
                // Select the entity
                setSelectedEntityId(targetEntity.id);
            }
        }
    }


    public void setSelectedEntityId(String entityId) {
        this.selectedEntityId = entityId;
        EntityDTO entity = gameService.getEntityDTO(entityId);

        // Ensure your GameView is updated to accept an ID/DTO instead of IGridEntity
         view.setSelectedEntity(entity);

        if (entityId != null) {
            Set<TileDTO> availableTiles = gameService.getAvailableTilesDTOForMovement(entityId);
            view.showSelectedEntityAvailableMoves(availableTiles);
        }
    }
}