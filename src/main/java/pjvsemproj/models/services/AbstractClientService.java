package pjvsemproj.models.services;

import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base implementation of the {@link ClientGameEngine} interface.
 * <p>
 * Extends the core game service to provide client-specific functionality,
 * including tracking the local player's identity and executing UI callbacks upon state changes.
 */
public class AbstractClientService extends AbstractGameService implements ClientGameEngine {

    protected String clientName;
    protected Consumer<String> onGameOver;
    protected Runnable onBoardUpdated;

    /**
     * Constructs the client service and automatically links the core game's
     * victory condition to the client's game-over callback.
     *
     * @param game the initial game state
     */
    public AbstractClientService(Game game) {
        super(game);

        super.addWinListener(winner -> {
            if (onGameOver != null) {
                onGameOver.accept(winner.getName());
            }
        });
    }

    /**
     * Registers the client's chosen display name.
     *
     * @param playerName the name the user wishes to be identified by
     */
    @Override
    public void login(String playerName) {
        clientName = playerName;
    }

    // TODO check if method ready() is needed in service or we should move it out from it
    /**
     * Signals that the client is ready to begin the match.
     */
    @Override
    public void ready() {

    }

    /**
     * Verifies if the local client is currently allowed to issue game commands.
     *
     * @return {@code true} if it is this client's turn to play
     */
    @Override
    public boolean isMyTurn() {
        return Objects.equals(
                turnManager.getCurrentPlayer().getName(),
                clientName
        );
    }

    /**
     * Gracefully disconnects the client from the game session,
     * determining the winner by default.
     */
    @Override
    public void quit() {
        // assigning some default value
        Player winner = game.getPlayers().getFirst();
        // searching for the next player after current player
        for (Player player : game.getPlayers()) {
            if (player != turnManager.getCurrentPlayer()) {
                winner = player;
                break;
            }
        }

        System.out.println("Game quit");
        notifyBoardUpdated();
    }

    /**
     * Binds a callback function that will be executed when the game reaches a victory state.
     *
     * @param callback a consumer function that accepts the winning player's name as an argument
     */
    @Override
    public void setOnGameOver(Consumer<String> callback) {
        this.onGameOver = callback;
    }

    /**
     * Retrieves the name currently registered to this client instance.
     *
     * @return the client's player name
     */
    @Override
    public String getClientName() {
        return clientName;
    }

    /**
     * Verifies if a specific troop unit is commanded by the local client.
     *
     * @param unitId the unique identifier of the troop unit to check
     * @return {@code true} if the unit's owner matches the local client's name
     */
    private boolean troopBelongsToClient(String unitId) {
        TroopUnit troopUnit = findTroopById(unitId);
        return Objects.equals(troopUnit.getOwner().getName(), clientName);
    }

    /**
     * Binds a callback function that will be executed whenever the internal game state changes.
     * Typically used to trigger UI repaints.
     *
     * @param callback a runnable task to execute upon state updates
     */
    @Override
    public void setOnBoardUpdated(Runnable callback) {
        onBoardUpdated = callback;
    }

    /**
     * Triggers the registered UI callback to repaint the screen and side panels.
     */
    protected void notifyBoardUpdated() {
        if (onBoardUpdated != null) {
            onBoardUpdated.run();
        }
    }

    /**
     * Attempts to teleport a military unit to a target coordinate and notifies the UI.
     *
     * @param unitId the unique identifier of the moving unit
     * @param x      the target X coordinate
     * @param y      the target Y coordinate
     * @return always {@code false} as currently implemented
     */
    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        super.moveUnit(unitId, x, y);
        notifyBoardUpdated();
        return false;
    }

    /**
     * Executes a combat action between two entities and notifies the UI.
     *
     * @param attackerId the unique identifier of the attacking unit
     * @param targetId   the unique identifier of the defending entity
     * @return always {@code false} as currently implemented
     */
    @Override
    public boolean attack(String attackerId, String targetId) {
        super.attack(attackerId, targetId);
        notifyBoardUpdated();
        return false;
    }

    /**
     * Purchases and spawns a new military unit at a specific city and notifies the UI.
     *
     * @param cityId    the unique identifier of the spawning city
     * @param troopType the classification of the unit to purchase
     * @return always {@code false} as currently implemented
     */
    @Override
    public boolean buyUnit(String cityId, String troopType) {
        super.buyUnit(cityId, troopType);
        notifyBoardUpdated();
        return false;
    }

    /**
     * Upgrades a specific city to its next economic tier and notifies the UI.
     *
     * @param cityId the unique identifier of the city to upgrade
     * @return always {@code false} as currently implemented
     */
    @Override
    public boolean upgradeCity(String cityId) {
        super.upgradeCity(cityId);
        notifyBoardUpdated();
        return false;
    }

    /**
     * Concludes the current player's turn and notifies the UI of the state change.
     */
    @Override
    public void endTurn() {
        super.endTurn();
        notifyBoardUpdated();
    }

    /**
     * Retrieves valid movement tiles with an added layer of client-side security.
     * Prevents the client from querying the reachable tiles of enemy units.
     *
     * @param unitId the unit to evaluate
     * @return a set of reachable tiles, or an empty set if the unit does not belong to the client
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForMovement(String unitId) {
        if (!troopBelongsToClient(unitId)) return new HashSet<>();
        return super.getAvailableTilesDTOForMovement(unitId);
    }

    /**
     * Retrieves valid attack tiles with an added layer of client-side security.
     * Prevents the client from querying the attack range of enemy units.
     *
     * @param unitId the unit to evaluate
     * @return a set of attackable tiles, or an empty set if the unit does not belong to the client
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForAttack(String unitId) {
        if (!troopBelongsToClient(unitId)) return new HashSet<>();
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    /**
     * Bypasses client-side security checks to retrieve attack tiles regardless of unit ownership.
     *
     * @param unitId the unit to evaluate
     * @return a set of attackable tiles
     */
    protected Set<TileDTO> getUnrestrictedAttackTiles(String unitId) {
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    /**
     * Bypasses client-side security checks to retrieve movement tiles regardless of unit ownership.
     *
     * @param unitId the unit to evaluate
     * @return a set of reachable tiles
     */
    protected Set<TileDTO> getUnrestrictedMovementTiles(String unitId) {
        return super.getAvailableTilesDTOForMovement(unitId);
    }
}