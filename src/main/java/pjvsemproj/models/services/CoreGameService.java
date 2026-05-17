package pjvsemproj.models.services;

import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TileDTO;

import java.util.List;
import java.util.Set;

/**
 * Defines the interface for interacting with the game.
 * <p>
 * Acts as an abstraction layer between UI/network and game logic.
 * This decouples presentation layouts from core backend evaluation layers, providing uniform commands
 * to execute tactical choices and fetch isolated state transfer blocks.
 */
public interface CoreGameService {

    /**
     * Requests relocation of a specific combat unit to designated target grid coordinates.
     *
     * @param unitId Unique lookup key string identifying the moving troop entity.
     * @param x      The destination column index position along the horizontal map axis.
     * @param y      The destination row index position along the vertical map axis.
     * @return {@code true} if the movement path is valid and coordinates were shifted successfully;
     * {@code false} if the action violates spatial rules or turn limitations.
     */
    boolean moveUnit(String unitId, int x, int y);

    /**
     * Initiates a local combat interaction between an offensive unit and a target unit.
     * <p>
     * The processing engine automatically computes damage variations based on structural boundaries.
     *
     * @param attackerId Unique lookup key identifying the attacking troop entity.
     * @param targetId   Unique lookup key identifying the defending troop entity target.
     * @return {@code true} if the combat execution conditions were fully satisfied and damage was dealt;
     * {@code false} if the action is illegal or targets are out of range.
     */
    boolean attack(String attackerId, String targetId);

    /**
     * Executes a network-synchronized combat action forcing an explicit authoritative health metric onto a target defender unit.
     * <p>
     * Typically dispatched by client-side receiver threads processing data packets sent down from a master server referee.
     *
     * @param attackerId Unique lookup key identifying the instigating offensive entity.
     * @param targetId   Unique lookup key identifying the target defending entity.
     * @param newHp      The explicit health point total declared by the server host instance to ensure game state sync.
     * @return {@code true} if state variables matched criteria and updates were applied; {@code false} otherwise.
     */
    boolean attack(String attackerId, String targetId, int newHp);

    /**
     * Requests the production and placement of a new military unit at a target city location.
     * <p>
     * The internal allocation routines verify financial account criteria automatically.
     *
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if player funds were sufficient, recruitment tiles were clear, and the unit was spawned;
     * {@code false} if validation rules reject production.
     */
    boolean buyUnit(String cityId, String troopType);

    /**
     * Forces the recruitment of a synchronized military unit containing a pre-allocated network identity key.
     * <p>
     * Crucial for maintaining deterministic distributed game states across remote network sockets.
     *
     * @param unitId    The authoritatively pre-generated identity key string to assign to the newly created troop asset.
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if the network synchronization creation criteria passed; {@code false} if rejected.
     */
    boolean buyUnitWithId(String unitId, String cityId, String troopType);

    /**
     * Advances the operational development level ranking tier of a specific city.
     *
     * @param cityId Unique string tracking token identifying the targeted settlement asset.
     * @return {@code true} if balance reserves matched upgrade costs and tier transformation succeeded;
     * {@code false} if funds are insufficient or development limits are hit.
     */
    boolean upgradeCity(String cityId);

    /**
     * Concludes the active turn block options for the current player participant.
     * <p>
     * Shifts control permissions over to subsequent rotation slots, handles financial generation intervals,
     * and refreshes exhaustion counters across entities.
     */
    void endTurn();

    /**
     * Extracts a comprehensive master data transfer wrapper snapshot of the total running match session state.
     *
     * @return A consolidated {@link GameDTO} container tracking bounds, player balances, and active map entities.
     */
    GameDTO getGameDTO();

    /**
     * Queries and fetches the isolated data transfer properties matching a single tracking identifier.
     *
     * @param entityId The unique identification token string of the targeted map entity asset.
     * @return The concrete {@link EntityDTO} representation model matching the key, or {@code null} if no entity matches.
     */
    EntityDTO getEntityDTO(String entityId);

    /**
     * Fetches the total column count dimension representing the horizontal game grid boundary.
     *
     * @return The map grid width dimension integer.
     */
    int getMapWidth();

    /**
     * Fetches the total row count dimension representing the vertical game grid boundary.
     *
     * @return The map grid height dimension integer.
     */
    int getMapHeight();

    /**
     * Composes an isolated container snapshot reflecting coordinate indexes and occupant structures at a specific grid location.
     *
     * @param x Horizontal column map grid coordinate index space.
     * @param y Vertical row map grid coordinate index space.
     * @return A data-safe {@link TileDTO} mapping characteristics matching the spatial cell query.
     */
    TileDTO getTileDTO(int x, int y);

    /**
     * Aggregates an itemized snapshot collection of tracking profiles matching all participants inside this match session.
     *
     * @return A {@link List} containing {@link PlayerDTO} data containers tracking names and balances.
     */
    List<PlayerDTO> getPlayersDTO();

    /**
     * Fetches a data container snapshot representing the participant whose turn execution sequence is currently active.
     *
     * @return A {@link PlayerDTO} tracking properties for the player holding active action clearances.
     */
    PlayerDTO getCurrentPlayerDTO();

    /**
     * Computes the mathematical reachability navigation path options matrix for a selected combat unit.
     * <p>
     * Leverages path search algorithms to filter out blocked or out-of-range layout components.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} containing safe {@link TileDTO} objects where navigation can conclude this turn.
     */
    Set<TileDTO> getAvailableTilesDTOForMovement(String unitId);

    /**
     * Computes the set of cell locations containing hostile forces that a unit can legally strike.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} containing {@link TileDTO} objects that house hit-eligible enemy targets.
     */
    Set<TileDTO> getAvailableTilesDTOForAttack(String unitId);
}