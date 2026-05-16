package pjvsemproj.models.services;

import pjvsemproj.dto.*;
import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;

import java.util.*;
import java.util.stream.Collectors;

// TODO remove current player from the game, cause we have double sources of truth then: turn manager and game (leave only manager)
/**
 * Base implementation of the {@link CoreGameService} interface.
 * <p>
 * Acts as a Facade that initializes all domain managers and wires them together.
 * Translates incoming requests involving string IDs into operations on domain entities,
 * returning safe Data Transfer Objects (DTOs) to the calling layer.
 */
public class AbstractGameService implements CoreGameService {

    protected final Game game;

    protected final MovementManager movementManager;
    protected final CombatManager combatManager;
    protected final EconomyManager economyManager;
    protected final TurnManager turnManager;
    protected final ConquestManager conquestManager;

    /**
     * Constructs the base game service, initializes the domain managers,
     * and wires all internal listeners together.
     *
     * @param game the initial game state containing the map and players
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
     * Attempts to move a unit to the specified coordinates.
     *
     * @param unitId the ID of the unit to move
     * @param x      the target X coordinate
     * @param y      the target Y coordinate
     * @return {@code true} if the movement was successful, {@code false} otherwise
     */
    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        TroopUnit troopUnit = findTroopById(unitId);
        Tile tile = game.getMap().getTile(x, y);

        return movementManager.moveTroopUnit(troopUnit, tile);
    }

    /**
     * Registers a listener to be notified when the conquest manager declares a winner.
     *
     * @param listener the win event listener
     */
    public void addWinListener(IWinListener listener) {
        conquestManager.addWinListener(listener);
    }

    /**
     * Attempts to execute an attack from one unit to another.
     *
     * @param attackerId the ID of the attacking unit
     * @param targetId   the ID of the target unit
     * @return {@code true} if the attack was successful, {@code false} otherwise
     */
    @Override
    public boolean attack(String attackerId, String targetId) {
        TroopUnit attacker = findTroopById(attackerId);
        TroopUnit target = findTroopById(targetId);

        return combatManager.attackTroop(attacker, target);
    }

    /**
     * Attempts to purchase a new troop unit and spawn it at the specified city.
     *
     * @param cityId    the ID of the city where the unit will spawn
     * @param troopType the type of troop to purchase
     * @return {@code true} if the purchase was successful, {@code false} otherwise
     */
    @Override
    public boolean buyUnit(String cityId, String troopType) {
        City city = findCityById(cityId);

        TroopType type = TroopType.valueOf(troopType);
        return economyManager.buyTroopUnit(type, city);
    }

    /**
     * Attempts to upgrade the specified city.
     *
     * @param cityId the ID of the city to upgrade
     * @return {@code true} if the upgrade was successful, {@code false} otherwise
     */
    @Override
    public boolean upgradeCity(String cityId) {
        City city = findCityById(cityId);

        return economyManager.upgradeCity(city);
    }

    /**
     * Ends the current active turn.
     */
    @Override
    public void endTurn() {
        turnManager.endTurn();
    }

    /**
     * Helper method to locate a specific troop unit by its unique identifier.
     *
     * @param id the string ID of the unit
     * @return the domain TroopUnit
     * @throws EntityNotFoundException if the unit does not exist in any player's roster
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
     * Helper method to locate a specific city by its unique identifier.
     *
     * @param id the string ID of the city
     * @return the domain City
     * @throws EntityNotFoundException if the city does not exist in any player's roster
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
     * Retrieves a snapshot of the current game state as a DTO.
     *
     * @return a GameDTO containing map dimensions, players, and entities
     */
    @Override
    public GameDTO getGameDTO() {
        return new GameDTO(game);
    }

    /**
     * Retrieves a snapshot of a specific entity as a DTO.
     *
     * @param entityId the ID of the entity
     * @return the EntityDTO, or {@code null} if not found
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
     * Retrieves the width of the map.
     *
     * @return the map width in tiles
     */
    @Override
    public int getMapWidth() {
        return game.getMap().getWidth();
    }

    /**
     * Retrieves the height of the map.
     *
     * @return the map height in tiles
     */
    @Override
    public int getMapHeight() {
        return game.getMap().getHeight();
    }

    /**
     * Retrieves a snapshot of a specific tile and its contents as a DTO.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return the TileDTO
     */
    @Override
    public TileDTO getTileDTO(int x, int y) {
        return new TileDTO(game.getMap().getTile(x, y));
    }

    /**
     * Retrieves a list of snapshots for all players currently in the game.
     *
     * @return a list of PlayerDTOs
     */
    @Override
    public List<PlayerDTO> getPlayersDTO() {
        List<PlayerDTO> playerDTOs = new ArrayList<>();
        playerDTOs.add(new PlayerDTO(game.getPlayers().getFirst()));
        playerDTOs.add(new PlayerDTO(game.getPlayers().getLast()));
        return playerDTOs;
    }

    /**
     * Retrieves a snapshot of the player whose turn is currently active.
     *
     * @return the current active PlayerDTO
     */
    @Override
    public PlayerDTO getCurrentPlayerDTO() {
        return new PlayerDTO(turnManager.getCurrentPlayer());
    }

    /**
     * Calculates and retrieves all available movement tiles for a specific unit as DTOs.
     *
     * @param unitId the ID of the unit
     * @return a set of reachable TileDTOs
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
     * Calculates and retrieves all available attack tiles for a specific unit as DTOs.
     *
     * @param unitId the ID of the unit
     * @return a set of attackable TileDTOs
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