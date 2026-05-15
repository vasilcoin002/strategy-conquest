package pjvsemproj.models.services;

import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TileDTO;

import java.util.List;
import java.util.Set;

/**
 * Defines the interface for interacting with the core game mechanics.
 * <p>
 * Acts as a strict abstraction layer between the UI/network components and the internal game logic.
 * By exclusively returning Data Transfer Objects (DTOs) instead of raw domain models,
 * this interface ensures the game state remains secure and immutable from the outside.
 */
public interface CoreGameService {

    /**
     * Attempts to teleport a military unit to a target coordinate.
     *
     * @param unitId the unique identifier of the moving unit
     * @param x      the target X coordinate
     * @param y      the target Y coordinate
     * @return {@code true} if the movement was legal and successful
     */
    boolean moveUnit(String unitId, int x, int y);

    /**
     * Executes a combat action between two entities.
     *
     * @param attackerId the unique identifier of the attacking unit
     * @param targetId   the unique identifier of the defending entity
     * @return {@code true} if the attack was legal and executed
     */
    boolean attack(String attackerId, String targetId);

    /**
     * Purchases and spawns a new military unit at a specific city.
     *
     * @param cityId    the unique identifier of the spawning city
     * @param troopType the classification of the unit to purchase (e.g., "Militia", "Cavalry")
     * @return {@code true} if the purchase was successful and funds were deducted
     */
    boolean buyUnit(String cityId, String troopType);

    /**
     * Upgrades a specific city to its next economic tier.
     *
     * @param cityId the unique identifier of the city to upgrade
     * @return {@code true} if the upgrade was successfully funded and applied
     */
    boolean upgradeCity(String cityId);

    /**
     * Concludes the current player's turn and passes control to the next player.
     */
    void endTurn();

    /**
     * Retrieves a complete, read-only snapshot of the current game state.
     *
     * @return a GameDTO containing map dimensions, players, and all entities
     */
    GameDTO getGameDTO();

    /**
     * Retrieves a read-only snapshot of a specific entity.
     *
     * @param entityId the unique identifier of the entity
     * @return the EntityDTO, or {@code null} if not found
     */
    EntityDTO getEntityDTO(String entityId);

    /**
     * Retrieves the total width of the game map.
     *
     * @return map width in tiles
     */
    int getMapWidth();

    /**
     * Retrieves the total height of the game map.
     *
     * @return map height in tiles
     */
    int getMapHeight();

    /**
     * Retrieves a read-only snapshot of a specific map tile and its contents.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return the TileDTO
     */
    TileDTO getTileDTO(int x, int y);

    /**
     * Retrieves a list of read-only snapshots for all players currently in the game.
     *
     * @return a list of PlayerDTOs
     */
    List<PlayerDTO> getPlayersDTO();

    /**
     * Retrieves a read-only snapshot of the player whose turn is currently active.
     *
     * @return the active PlayerDTO
     */
    PlayerDTO getCurrentPlayerDTO();

    /**
     * Calculates and retrieves all valid map tiles a specific unit can traverse this turn.
     *
     * @param unitId the unique identifier of the moving unit
     * @return a set of reachable TileDTOs
     */
    Set<TileDTO> getAvailableTilesDTOForMovement(String unitId);

    /**
     * Calculates and retrieves all valid map tiles containing enemies a specific unit can attack.
     *
     * @param unitId the unique identifier of the attacking unit
     * @return a set of attackable TileDTOs
     */
    Set<TileDTO> getAvailableTilesDTOForAttack(String unitId);

}