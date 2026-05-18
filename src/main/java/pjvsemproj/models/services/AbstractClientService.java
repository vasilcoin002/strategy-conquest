package pjvsemproj.models.services;

import pjvsemproj.dto.TileDTO;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Intermediate orchestration subclass layering localization rules over core simulation services.
 * <p>
 * Implements {@link ClientGameEngine} to intercept game interaction inputs, enforces ownership checks
 * to block cross-user operations, and dispatches UI thread component refresh callbacks.
 */
public class AbstractClientService extends AbstractGameService implements ClientGameEngine {
    private static final Logger LOGGER = Logger.getLogger(AbstractClientService.class.getName());

    protected String clientName;
    protected Consumer<String> onGameOver;
    protected Runnable onBoardUpdated;

    /**
     * Constructs a client service orchestration instance and registers structural game-over observers.
     *
     * @param game The root domain {@link Game} context model managed by this client wrapper.
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
     * Assigns player identification tokens inside local context tracking states.
     *
     * @param playerName The chosen username credential used by the human local player.
     */
    @Override
    public void login(String playerName) {
        clientName = playerName;
    }

    /**
     * Standard status hook to fire sync sequences once components complete initialization.
     */
    @Override
    public void ready() {

    }

    /**
     * Checks if the user tracking identifier matches the participant holding active turn properties.
     *
     * @return {@code true} if turn indices align with the local client name; {@code false} if input options should lock.
     */
    @Override
    public boolean isMyTurn() {
        return Objects.equals(
                turnManager.getCurrentPlayer().getName(),
                clientName
        );
    }

    /**
     * Executes local resignation sequences, handles instant victory scoring calculations, and triggers game over callbacks.
     * <p>
     * Searches for the remaining opponent in the player list to declare them the absolute winner of the match.
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
        conquestManager.announceWinner(winner);

        System.out.println("Game quit");
    }

    /**
     * Registers intercept closures to execute interface navigation changes upon game-over conditions.
     *
     * @param callback A {@link Consumer} closure handling string values matching the winner's name profile.
     */
    @Override
    public void setOnGameOver(Consumer<String> callback) {
        this.onGameOver = callback;
    }

    /**
     * Fetches the unique username identifier mapped onto this local machine context state.
     *
     * @return The local client string name token value.
     */
    @Override
    public String getClientName() {
        return clientName;
    }

    /**
     * Helper mapping method to check if an identity credential matches an asset ownership reference.
     *
     * @param unitId Unique lookup key token string identifying the targeted mobile troop.
     * @return {@code true} if the unit belongs to the local client name; {@code false} if owned by a bot or rival.
     */
    private boolean troopBelongsToClient(String unitId) {
        TroopUnit troopUnit = findTroopById(unitId);
        return Objects.equals(troopUnit.getOwner().getName(), clientName);
    }

    /**
     * Registers rendering trigger listeners to process board model layout modifications.
     *
     * @param callback A {@link Runnable} closure containing UI redrawing directives.
     */
    @Override
    public void setOnBoardUpdated(Runnable callback) {
        onBoardUpdated = callback;
    }

    /**
     * Dispatches rendering execution signals down onto registered interface listeners.
     */
    protected void notifyBoardUpdated() {
        if (onBoardUpdated != null) {
            onBoardUpdated.run();
        }
    }

    /**
     * Requests relocation of a combat unit and triggers local map redraw signals.
     *
     * @param unitId Unique lookup key string identifying the moving troop entity.
     * @param x      The destination column index position along the horizontal map axis.
     * @param y      The destination row index position along the vertical map axis.
     * @return {@code true} if the position state attributes updated successfully; {@code false} otherwise.
     */
    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        super.moveUnit(unitId, x, y);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Dispatches combat engagement calculations and fires rendering layout sweeps.
     *
     * @param attackerId Unique lookup key identifying the attacking troop entity.
     * @param targetId   Unique lookup key identifying the defending troop entity target.
     * @return {@code true} if health levels updated and targets adjusted successfully; {@code false} otherwise.
     */
    @Override
    public boolean attack(String attackerId, String targetId) {
        super.attack(attackerId, targetId);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Enforces authoritative network-synchronized health totals onto defenders and updates view panels.
     *
     * @param attackerId Unique lookup key identifying the instigating offensive entity.
     * @param targetId   Unique lookup key identifying the target defending entity.
     * @param newHp      The explicit health point total declared authoritatively by the remote host referee.
     * @return {@code true} if state variables matched criteria and updates were applied; {@code false} otherwise.
     */
    @Override
    public boolean attack(String attackerId, String targetId, int newHp) {
        super.attack(attackerId, targetId, newHp);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Requests the production of a new military unit at a target city and updates board graphics.
     *
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if recruitment requirements were satisfied and the unit spawned; {@code false} otherwise.
     */
    @Override
    public boolean buyUnit(String cityId, String troopType) {
        super.buyUnit(cityId, troopType);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Forces the recruitment of a synchronized military unit containing a pre-allocated network identity key and redraws the grid.
     *
     * @param unitId    The authoritatively pre-generated identity key string to assign to the newly created troop asset.
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class metadata descriptor string tracking the target troop configuration to produce.
     * @return {@code true} if network creation criteria passed; {@code false} otherwise.
     */
    @Override
    public boolean buyUnitWithId(String unitId, String cityId, String troopType) {
        super.buyUnitWithId(unitId, cityId, troopType);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Advances the active tier development rank of a target settlement structure and requests an interface refresh.
     *
     * @param cityId Unique string tracking token identifying the targeted settlement asset.
     * @return {@code true} if tier transformation modifications succeeded; {@code false} otherwise.
     */
    @Override
    public boolean upgradeCity(String cityId) {
        super.upgradeCity(cityId);
        notifyBoardUpdated();
        return true;
    }

    /**
     * Concludes turn block choices, rotates permission flags, and triggers UI updates.
     */
    @Override
    public void endTurn() {
        super.endTurn();
        notifyBoardUpdated();
    }

    /**
     * Intercepts movement availability requests and blocks path calculation arrays if ownership criteria fail.
     * <p>
     * Provides comprehensive debug diagnostic outputs highlighting unit credentials, turn tokens, and execution states.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} of reachability cells, or an empty collection if the unit belongs to an opponent.
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForMovement(String unitId) {
        TroopUnit troop = findTroopById(unitId);

        LOGGER.info("=== MOVE DEBUG ===");
        LOGGER.info("clientName = " + clientName);
        LOGGER.info("troop owner = " + troop.getOwner().getName());
        LOGGER.info("current player = " + turnManager.getCurrentPlayer().getName());
        LOGGER.info("moved = " + troop.hasMovedThisTurn());

        if (!troopBelongsToClient(unitId)) {
            LOGGER.warning("BLOCKED: troop does not belong to client");
            return new HashSet<>();
        }

        Set<TileDTO> result = super.getAvailableTilesDTOForMovement(unitId);
        LOGGER.info("move tiles count = " + result.size());

        return result;
    }

    /**
     * Intercepts combat option requests and drops target tracking calculations if the unit belongs to an opponent.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} of attackable coordinates, or an empty collection if selection validation fails.
     */
    @Override
    public Set<TileDTO> getAvailableTilesDTOForAttack(String unitId) {
        if (!troopBelongsToClient(unitId)) return new HashSet<>();
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    /**
     * Bypasses client ownership verification layers to query unconstrained combat options directly from the base simulation service.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} of attackable coordinate blocks.
     */
    protected Set<TileDTO> getUnrestrictedAttackTiles(String unitId) {
        return super.getAvailableTilesDTOForAttack(unitId);
    }

    /**
     * Bypasses client ownership verification layers to query unconstrained movement options directly from the base simulation service.
     *
     * @param unitId Unique lookup token string matching the mobile troop entity to evaluate.
     * @return A {@link Set} of reachable navigation blocks.
     */
    protected Set<TileDTO> getUnrestrictedMovementTiles(String unitId) {
        return super.getAvailableTilesDTOForMovement(unitId);
    }

    /**
     * Directly injects an identification username token into internal state tracking fields.
     *
     * @param playerName The user profile name string to store as the local client owner identity.
     */
    @Override
    public void setLocalClientName(String playerName) {
        this.clientName = playerName;
    }
}