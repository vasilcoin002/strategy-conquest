package pjvsemproj.models.services;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the core game interaction abstraction.
 * <p>
 * Aggregates and coordinates specialized sub-managers (movement, combat, economy, turns, and conquests)
 * to execute foundational simulation rule checks and modify the live in-memory game state.
 */
public class AbstractGameService implements CoreGameService {

    protected final Game game;
    protected final MovementManager movementManager;
    protected final CombatManager combatManager;
    protected final EconomyManager economyManager;
    protected final TurnManager turnManager;
    protected final ConquestManager conquestManager;

    /**
     * Constructs a baseline game service instance and attaches state synchronizers and lifecycle listeners.
     * <p>
     * Initializes all underlying sub-manager modules and configures anonymous turn listeners
     * to keep the master game context consistently updated during turn rotations.
     *
     * @param game The root domain {@link Game} simulation instance containing active players, maps, and assets.
     */
    public AbstractGameService(Game game) {
        this.game = game;

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        this.turnManager = new TurnManager(player1, player2, game.getCurrentPlayer());

        this.conquestManager = new ConquestManager(game.getPlayers(), turnManager.getCurrentPlayer());
        this.movementManager = new MovementManager(
                game.getMap(), turnManager.getCurrentPlayer(), conquestManager);

        this.combatManager = new CombatManager(this.game.getMap(), turnManager.getCurrentPlayer());
        this.economyManager = new EconomyManager(turnManager.getCurrentPlayer());

        this.turnManager.addTurnListener(new ITurnListener() {
            @Override
            public void onTurnStart(Player activePlayer) {
                game.setCurrentPlayer(activePlayer); // Syncs the state
            }

            @Override
            public void onTurnEnd(Player endingPlayer) {}
        });

        this.turnManager.addTurnListener(movementManager);
        this.turnManager.addTurnListener(combatManager);
        this.turnManager.addTurnListener(economyManager);
        this.turnManager.addTurnListener(conquestManager);
    }

    /**
     * Relocates a combat unit to specific target cell coordinates if verified by path routing rules.
     *
     * @param unitId Unique lookup key identifying the mobile troop entity to relocate.
     * @param x      Target destination column index position along the horizontal map axis.
     * @param y      Target destination row index position along the vertical map axis.
     * @return {@code true} if the path is open and the unit was successfully moved;
     * {@code false} if the path is invalid or out of reach.
     * @throws EntityNotFoundException if the provided unitId does not match any active troop.
     */
    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        TroopUnit troopUnit = findTroopById(unitId);

        Tile tile = game.getMap().getTile(x, y);
        Set<Tile> availableTiles = movementManager.getAvailableTilesForMovement(troopUnit);

        if (!availableTiles.contains(tile)) {
            return false;
        }
        return movementManager.moveTroopUnit(troopUnit, tile);
    }

    /**
     * Registers a victory listener observer callback onto the conquest tracking layer.
     *
     * @param listener The {@link IWinListener} subscriber monitoring match resolution criteria.
     */
    public void addWinListener(IWinListener listener) {
        conquestManager.addWinListener(listener);
    }

    /**
     * Executes local combat encounters between an instigating unit and a defending unit.
     *
     * @param attackerId Unique lookup key identifying the attacking troop entity.
     * @param targetId   Unique lookup key identifying the defending troop entity target.
     * @return {@code true} if combat calculations were performed and health points deducted successfully;
     * {@code false} if the engagement is rejected by game rules.
     * @throws EntityNotFoundException if either identifier fails to resolve to an active troop.
     */
    @Override
    public boolean attack(String attackerId, String targetId) {
        TroopUnit attacker = findTroopById(attackerId);
        TroopUnit target = findTroopById(targetId);

        return combatManager.attackTroop(attacker, target);
    }

    /**
     * Executes a combat action and applies an explicit authoritative health metric onto a defending unit.
     *
     * @param attackerId Unique lookup key identifying the instigating offensive entity.
     * @param targetId   Unique lookup key identifying the target defending entity.
     * @param newHp      The explicit health point total declared authoritatively by the remote host referee.
     * @return {@code true} if synchronization parameters matched rules and health states updated successfully; {@code false} otherwise.
     * @throws EntityNotFoundException if either identifier fails to resolve to an active troop.
     */
    @Override
    public boolean attack(String attackerId, String targetId, int newHp) {
        TroopUnit attacker = findTroopById(attackerId);
        TroopUnit target = findTroopById(targetId);

        return combatManager.attackTroop(attacker, target, newHp);
    }

    /**
     * Requests the production and placement of a new unit at a target city location.
     *
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if recruitment requirements were satisfied and the unit spawned;
     * {@code false} if rejected due to blocked tiles or insufficient funds.
     * @throws EntityNotFoundException if the provided cityId fails to resolve.
     * @throws IllegalArgumentException if the provided troopType string is malformed or invalid.
     */
    @Override
    public boolean buyUnit(String cityId, String troopType) {
        City city = findCityById(cityId);

        TroopType type = TroopType.valueOf(troopType);
        return economyManager.buyTroopUnit(type, city);
    }

    /**
     * Forces the recruitment of a synchronized military unit containing a pre-allocated network identity key.
     * <p>
     * Verifies that the proposed unique ID string does not conflict with existing assets before spawning.
     *
     * @param unitId    The authoritatively pre-generated identity key string to assign to the newly created troop asset.
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if identity verification checks passed and reproduction succeeded;
     * {@code false} if the provided identity key is already taken or parameters are invalid.
     * @throws EntityNotFoundException if the provided cityId fails to resolve.
     */
    @Override
    public boolean buyUnitWithId(String unitId, String cityId, String troopType) {
        boolean idIsUnique = false;
        try {
            findTroopById(unitId);
        } catch (Exception e) {
            try {
                findCityById(unitId);
            } catch (Exception ex) {
                idIsUnique = true;
            }
        }
        if (!idIsUnique) {
            return false;
        }

        City city = findCityById(cityId);
        TroopType type = TroopType.valueOf(troopType);
        return economyManager.buyTroopUnitWithId(unitId, type, city);
    }

    /**
     * Upgrades the development level ranking tier of a specific city.
     *
     * @param cityId Unique string tracking token identifying the targeted settlement asset.
     * @return {@code true} if structural financial rules were met and the tier successfully updated;
     * {@code false} if funds are insufficient or development limits are hit.
     * @throws EntityNotFoundException if the provided cityId fails to resolve.
     */
    @Override
    public boolean upgradeCity(String cityId) {
        City city = findCityById(cityId);

        return economyManager.upgradeCity(city);
    }

    /**
     * Concludes option blocks for the active participant and rotates tournament positioning flags.
     */
    @Override
    public void endTurn() {
        turnManager.endTurn();
    }

    /**
     * Traverses rosters to locate a specific active unit reference matching the tracking key string.
     *
     * @param id Unique identification lookup key token string.
     * @return The concrete live {@link TroopUnit} domain model matching the key.
     * @throws EntityNotFoundException if the lookup target is missing from all rosters.
     */
    protected TroopUnit findTroopById(String id) {
        for (Player player : game.getPlayers()) {
            for (TroopUnit troopUnit : player.getTroops()) {
                if (troopUnit.getId().equals(id)) {
                    return troopUnit;
                }
            }
        }
        throw new EntityNotFoundException("TROOP_UNIT", id);
    }

    /**
     * Traverses rosters to locate a specific active city reference matching the tracking key string.
     *
     * @param id Unique identification lookup key token string.
     * @return The concrete live {@link City} domain model matching the key.
     * @throws EntityNotFoundException if the lookup target is missing from all rosters.
     */
    protected City findCityById(String id) {
        for (Player player : game.getPlayers()) {
            for (City city : player.getCities()) {
                if (city.getId().equals(id)) {
                    return city;
                }
            }
        }
        throw new EntityNotFoundException("CITY", id);
    }

    /**
     * Compiles a comprehensive snapshot representation of the master state parameters.
     *
     * @return A consolidated data transfer {@link GameDTO} state mapping.
     */
    @Override
    public GameDTO getGameDTO() {
        return new GameDTO(game);
    }

    /**
     * Queries player collections to construct data transfer snapshot instances matching a generic identifier.
     *
     * @param entityId The unique identification token string of the targeted map entity asset.
     * @return An appropriate downcasted implementation of {@link EntityDTO}, or {@code null} if missing.
     */
    @Override
    public EntityDTO getEntityDTO(String entityId) {
        for (Player player : game.getPlayers()) {
            for (TroopUnit troopUnit : player.getTroops()) {
                if (Objects.equals(troopUnit.getId(), entityId)) {
                    return new TroopUnitDTO(troopUnit);
                }
            }
            for (City city : player.getCities()) {
                if (Objects.equals(city.getId(), entityId)) {
                    return new CityDTO(city);
                }
            }
        }
        return null;
    }

    /**
     * Fetches the total column layout boundary representation tracking limits of the game board.
     *
     * @return Map column width integer count values.
     */
    @Override
    public int getMapWidth() {
        return game.getMap().getWidth();
    }

    /**
     * Fetches the total row layout boundary representation tracking limits of the game board.
     *
     * @return Map row height integer count values.
     */
    @Override
    public int getMapHeight() {
        return game.getMap().getHeight();
    }

    /**
     * Composes an isolated container snapshot reflecting the coordinates and elements of a specific grid position.
     *
     * @param x Horizontal column map grid coordinate index space.
     * @param y Vertical row map grid coordinate index space.
     * @return A data-safe {@link TileDTO} wrapper.
     */
    @Override
    public TileDTO getTileDTO(int x, int y) {
        return new TileDTO(game.getMap().getTile(x, y));
    }

    /**
     * Aggregates an itemized snapshot collection tracking all active participants inside this match session.
     *
     * @return A {@link List} containing bounded {@link PlayerDTO} structures.
     */
    @Override
    public List<PlayerDTO> getPlayersDTO() {
        List<PlayerDTO> playerDTOs = new ArrayList<>();
        playerDTOs.add(new PlayerDTO(game.getPlayers().getFirst()));
        playerDTOs.add(new PlayerDTO(game.getPlayers().getLast()));
        return playerDTOs;
    }

    /**
     * Fetches a snapshot container showing metrics of the participant holding turn action clearances.
     *
     * @return A {@link PlayerDTO} tracking properties for the currently active player.
     */
    @Override
    public PlayerDTO getCurrentPlayerDTO() {
        return new PlayerDTO(turnManager.getCurrentPlayer());
    }

    /**
     * Computes path reachability option grids for a selected mobile unit.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} containing safe {@link TileDTO} objects where navigation can conclude this turn.
     * @throws EntityNotFoundException if the provided unitId fails to resolve.
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForMovement(String unitId) {
        Set<Tile> availableTiles = movementManager
                .getAvailableTilesForMovement(findTroopById(unitId));
        return availableTiles.stream()
                .map(TileDTO::new)
                .collect(Collectors.toSet());
    }

    /**
     * Computes the collection of cell regions containing hostile targets that a unit can legally strike.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} containing {@link TileDTO} objects that house hit-eligible enemy targets.
     * @throws EntityNotFoundException if the provided unitId fails to resolve.
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForAttack(String unitId) {
        Set<Tile> availableTiles = combatManager
                .getAttackableTiles(findTroopById(unitId));
        return availableTiles.stream()
                .map(TileDTO::new)
                .collect(Collectors.toSet());
    }
}