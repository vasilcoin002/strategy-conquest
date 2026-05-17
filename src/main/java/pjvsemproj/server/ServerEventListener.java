package pjvsemproj.server;

/**
 * Contract defining all incoming server replication and notification event triggers.
 * <p>
 * Implemented by client-side event listeners to process incoming decoded text packets
 * from the network streams and translate them into visual updates or state sync actions.
 */
public interface ServerEventListener {

    /**
     * Fired when the server confirms a valid unit movement relocation action.
     *
     * @param unitId Unique lookup token string identifying the moved troop entity.
     * @param x      The authoritative destination column position along the map's horizontal axis.
     * @param y      The authoritative destination row position along the map's vertical axis.
     */
    void onUnitMoved(String unitId, int x, int y);

    /**
     * Fired when the server confirms a valid attack encounter between two mobile assets.
     *
     * @param attackerId Unique tracking lookup key matching the attacking offensive unit.
     * @param targetId   Unique tracking lookup key matching the defending unit asset target.
     * @param newHp      The authoritative remaining health points calculated by the server referee layer.
     */
    void onUnitAttacked(
            String attackerId,
            String targetId,
            int newHp
    );

    /**
     * Fired when a combat entity sustains fatal damage from an engagement.
     *
     * @param unitId Unique lookup token string identifying the deceased military unit to remove.
     */
    void onUnitDied(String unitId);

    /**
     * Fired when turn permissions are rotated and a new active player turn block begins.
     *
     * @param playerName The profile username identifying the participant who now holds input clearances.
     */
    void onTurnStarted(String playerName);

    /**
     * Fired when a city successfully expands its developmental ranking tier.
     *
     * @param cityId Unique string token identifying the structural settlement asset that was upgraded.
     */
    void onCityUpgraded(String cityId);

    /**
     * Fired when a participant purchases a unit containing an authoritatively pre-allocated network identification key.
     *
     * @param cityId    Unique identification token of the producing settlement structure asset.
     * @param unitId    The authoritatively pre-generated identity key string assigned to the newly created troop.
     * @param troopType Class configuration metadata descriptor string tracking the specific military type built.
     */
    void onUnitBought(
            String cityId,
            String unitId,
            String troopType
    );

    /**
     * Fired when final victory condition thresholds are met and an absolute winner emerges.
     *
     * @param winnerName The distinct username string identifying the victorious player profile.
     */
    void onGameOver(String winnerName);
}