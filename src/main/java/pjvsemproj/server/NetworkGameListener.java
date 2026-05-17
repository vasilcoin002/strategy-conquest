package pjvsemproj.server;

import pjvsemproj.models.services.NetworkGameService;

/**
 * Concrete implementation of the server event receiver contract for live multiplayer matches.
 * <p>
 * This class catches verified data updates parsed by the network client and passes them
 * downstream to the client engine proxy ({@link NetworkGameService}) to update the local mirrored simulation.
 */
public class NetworkGameListener implements ServerEventListener {

    private final NetworkGameService service;

    /**
     * Constructs a network game listener instance bound to a target game service proxy.
     *
     * @param service The network game engine orchestration service that processes state synchronizations.
     */
    public NetworkGameListener(NetworkGameService service) {
        this.service = service;
    }

    /**
     * Delegates an authoritative server unit movement confirmation down to the engine service layer.
     *
     * @param unitId Unique lookup token string identifying the moved troop entity.
     * @param x      The authoritative destination column position along the map's horizontal axis.
     * @param y      The authoritative destination row position along the map's vertical axis.
     */
    @Override
    public void onUnitMoved(String unitId, int x, int y) {
        service.applyServerMove(unitId, x, y);
    }

    /**
     * Delegates an authoritative combat calculation update to synchronize client health statistics.
     *
     * @param attackerId Unique tracking lookup key matching the attacking offensive unit.
     * @param targetId   Unique tracking lookup key matching the defending unit asset target.
     * @param newHp      The authoritative remaining health points calculated by the server referee layer.
     */
    @Override
    public void onUnitAttacked(String attackerId, String targetId, int newHp) {
        service.applyServerAttack(attackerId, targetId, newHp);
    }

    /**
     * Signals the local service module to permanently purge a destroyed unit from the grid and client rosters.
     *
     * @param unitId Unique lookup token string identifying the deceased military unit to remove.
     */
    @Override
    public void onUnitDied(String unitId) {
        service.applyServerUnitDeath(unitId);
    }

    /**
     * Signals the game service engine to rotate turn managers following authoritative server instructions.
     *
     * @param playerName The profile username identifying the participant who now holds input clearances.
     */
    @Override
    public void onTurnStarted(String playerName) {
        service.applyServerTurnStarted();
    }

    /**
     * Signals the game service engine to advance a target settlement's tier development rank.
     *
     * @param cityId Unique string token identifying the structural settlement asset that was upgraded.
     */
    @Override
    public void onCityUpgraded(String cityId) {
        service.applyServerCityUpgrade(cityId);
    }

    /**
     * Signals the client engine to instantiate a newly purchased unit with an authoritatively pre-allocated network ID.
     *
     * @param cityId    Unique identification token of the producing settlement structure asset.
     * @param unitId    The server-allocated unique identity key string to assign to the newly created troop.
     * @param troopType Class configuration metadata descriptor string tracking the specific military type built.
     */
    @Override
    public void onUnitBought(
            String cityId,
            String unitId,
            String troopType
    ) {
        service.applyServerUnitBought(
                cityId,
                unitId,
                troopType
        );
    }

    /**
     * Captures match resolution thresholds from the server and forwards them to open the game-over interface view.
     *
     * @param winnerName The distinct username string identifying the victorious player profile.
     */
    @Override
    public void onGameOver(String winnerName) {
        service.applyServerGameOver(winnerName);
    }
}