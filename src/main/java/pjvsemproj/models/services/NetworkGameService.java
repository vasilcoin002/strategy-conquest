package pjvsemproj.models.services;

import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.server.Client;
import pjvsemproj.server.NetworkGameListener;
import pjvsemproj.server.Protocol;

/**
 * Network-based implementation of GameService.
 * <p>
 * Instead of executing tactical commands locally inside shared memories, this service intercepts user action inputs,
 * serializes requests into network protocols, and routes instructions to a remote server host for authoritative evaluation.
 */
public class NetworkGameService extends AbstractClientService {

    private final Client client;

    /**
     * Constructs a network service engine instance and initializes socket event observers.
     *
     * @param client The underlying active communications {@link Client} node processing socket input/output streams.
     * @param game   The client-side mirror reproduction framework of the master {@link Game} simulation state.
     */
    public NetworkGameService(Client client, Game game) {
        super(game);
        this.client = client;

        client.setServerEventListener(new NetworkGameListener(this) {
        });
    }

    /**
     * Dispatches login credential verification packets across open server connection pipelines.
     *
     * @param playerName The custom username selected by the human participant to register on the server.
     */
    @Override
    public void login(String playerName) {
        client.sendToServer(Protocol.LOGIN, playerName);
        super.login(playerName);
    }

    /**
     * Commits readiness notification tokens to the remote host server to confirm synchronization availability.
     */
    @Override
    public void ready() {
        client.ready();
        super.ready();
    }

    /**
     * Forwards a unit relocation request to the server instead of applying positional shifts locally.
     *
     * @param unitId Unique lookup key string identifying the moving troop entity.
     * @param x      Target destination column index position along the horizontal map axis.
     * @param y      Target destination row index position along the vertical map axis.
     * @return {@code true} authoritatively to fulfill the local method execution boundary signature.
     */
    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        client.moveUnit(unitId, x, y);
        return true;
    }

    /**
     * Forwards an offensive engagement request to the server instead of applying damage updates locally.
     *
     * @param attackerId Unique lookup key identifying the attacking troop entity.
     * @param targetId   Unique lookup key identifying the defending troop entity target.
     * @return {@code true} authoritatively to fulfill the local method execution contract requirements.
     */
    @Override
    public boolean attack(String attackerId, String targetId) {
        client.attack(attackerId, targetId);
        return true;
    }

    /**
     * Forwards a troop production recruitment request down onto server network messaging links.
     *
     * @param cityId    Unique string token lookup key matching the producing settlement structure.
     * @param troopType Class configuration metadata descriptor string tracking the target troop class to produce.
     * @return {@code true} authoritatively to satisfy method signature criteria.
     */
    @Override
    public boolean buyUnit(String cityId, String troopType) {
        client.buyUnit(cityId, troopType);
        return true;
    }

    /**
     * Forwards a structural tier upgrade request to the authoritative network host.
     *
     * @param cityId Unique string tracking token identifying the targeted settlement asset.
     * @return {@code true} authoritatively to complete local processing interface calls.
     */
    @Override
    public boolean upgradeCity(String cityId) {
        client.upgradeCity(cityId);
        return true;
    }

    /**
     * Requests turn finalization authorization rules from the remote server referee.
     * <p>
     * CRITICAL: This bypasses the immediate execution of {@code super.endTurn()}.
     * Turn transitions are locked until the server authoritatively broadcasts back confirmation tokens.
     */
    @Override
    public void endTurn() {
        client.endTurn();
    }

    /**
     * Dispatches voluntary match surrender indicators to close remote session allocations cleanly.
     */
    @Override
    public void quit() {
        client.quit();
    }

    /**
     * Authoritatively updates local grid structures when the server confirms a unit movement action.
     *
     * @param unitId Unique identification token of the relocated troop entity.
     * @param x      Authoritative horizontal column destination grid coordinate index.
     * @param y      Authoritative vertical row destination grid coordinate index.
     */
    public void applyServerMove(String unitId, int x, int y) {
        super.moveUnit(unitId, x, y);
    }

    /**
     * Authoritatively synchronizes local health stats when the server confirms an attack encounter.
     *
     * @param attackerId Unique verification key matching the instigating offensive unit.
     * @param targetId   Unique verification key matching the defending unit asset.
     * @param newHp      Explicit health point totals declared by the remote host referee.
     */
    public void applyServerAttack(String attackerId, String targetId, int newHp){
        super.attack(attackerId, targetId, newHp);
    }

    /**
     * Authoritatively purges a unit from local grid contexts following a fatal server engagement.
     * <p>
     * Clears reference hooks from participant ownership trees, drops layout links from viewboards,
     * and signals the UI window to initiate a visual re-render sweep.
     *
     * @param unitId Unique lookup token string identifying the deceased military asset.
     */
    public void applyServerUnitDeath(String unitId) {
        TroopUnit troop = findTroopById(unitId);

        OwnershipHelper.removeTroopUnitFromPlayer(troop);
        GridPositionHelper.removeFromBoard(troop);

        notifyBoardUpdated();
    }

    /**
     * Directs the client simulation engine to rotate turn trackers following server instructions.
     */
    public void applyServerTurnStarted() {
        super.endTurn();
    }

    /**
     * Directs the client engine to advance settlement tier development ranks following server instructions.
     *
     * @param cityId Unique string token identifying the structural asset to upgrade.
     */
    public void applyServerCityUpgrade(String cityId) {
        super.upgradeCity(cityId);
    }

    /**
     * Directs the client engine to spawn a newly purchased unit with an explicit pre-allocated identity key.
     *
     * @param cityId    Unique identification token of the producing settlement asset.
     * @param unitId    The server-allocated identity token assigned to the newly created troop.
     * @param troopType Class configuration descriptor string matching the produced military type.
     */
    public void applyServerUnitBought(
            String cityId,
            String unitId,
            String troopType
    ) {
        super.buyUnitWithId(unitId, cityId, troopType);
    }

    /**
     * Intercepts match completion metrics from the network layer and dispatches victory overlays.
     * <p>
     * Evaluates the presence of registered observers to open completion frames on the JavaFX thread.
     *
     * @param winnerName Profile username string identifying the match winner.
     */
    public void applyServerGameOver(String winnerName) {
        System.out.println("Service triggering UI for " + winnerName);

        if (this.onGameOver != null) {
            this.onGameOver.accept(winnerName);
        } else {
            System.err.println("CRITICAL UI ERROR: The onGameOver listener is NULL. GameController did not connect it!");
        }
    }

    /**
     * Explicitly tells the server that the game was naturally won, prompting the server to shut down safely.
     *
     * @param winnerName Profile username string identifying the victorious participant.
     */
    public void notifyServerOfWin(String winnerName) {
        client.sendToServer(Protocol.GAME_OVER, winnerName);
    }
}