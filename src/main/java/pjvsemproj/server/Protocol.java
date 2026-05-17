package pjvsemproj.server;

/**
 * Defines all communication commands and network message headers shared between the client and server.
 * <p>
 * This enumeration acts as the application layer contract, dictating how payload text packets
 * are encoded, transmitted, and decoded across network socket streams.
 * Commands are split into client requests, server responses, and game state replication broadcasts.
 */
public enum Protocol {

    /**
     * Client request to authenticate and reserve a player session name slot in the lobby registry.
     * <p>
     * <b>Format:</b> {@code LOGIN|playerName}
     */
    LOGIN,

    /**
     * Client notification declaring readiness to synchronize and boot into the active game arena.
     * <p>
     * <b>Format:</b> {@code READY}
     */
    READY,

    /**
     * Notification indicating a user is voluntarily surrendering or severing the active connection channel.
     * <p>
     * <b>Format:</b> {@code QUIT}
     */
    QUIT,

    /**
     * Client request to execute a tactical unit move action to destination coordinates.
     * <p>
     * <b>Format:</b> {@code MOVE|unitId|x|y}
     */
    MOVE,

    /**
     * Client request to initiate an offensive engagement encounter against a targeted hostile unit.
     * <p>
     * <b>Format:</b> {@code ATTACK|attackerId|targetId}
     */
    ATTACK,

    /**
     * Client request to spend resources and spawn a new military unit type inside an owned settlement.
     * <p>
     * <b>Format:</b> {@code BUY_UNIT|cityId|troopType}
     */
    BUY_UNIT,

    /**
     * Client request to spend resources and advance the active development tier ranking of a city structure.
     * <p>
     * <b>Format:</b> {@code UPGRADE_CITY|cityId}
     */
    UPGRADE_CITY,

    /**
     * Client request to conclude active turn choices and pass control metrics over to the subsequent player.
     * <p>
     * <b>Format:</b> {@code END_TURN}
     */
    END_TURN,

    /**
     * Server acknowledgment reply confirming that a requested action sequence was processed successfully.
     * <p>
     * <b>Format:</b> {@code OK|messageDetails}
     */
    OK,

    /**
     * Server response code indicating validation breakdowns, syntax errors, or illegal move interactions.
     * <p>
     * <b>Format:</b> {@code ERROR|errorMessageReason}
     */
    ERROR,

    /**
     * Server broadcast delivering a fully serialized JSON string payload representing the comprehensive game level snapshot layout.
     * <p>
     * <b>Format:</b> {@code GAME_STATE|jsonPayloadString}
     */
    GAME_STATE,

    /**
     * Server notification broadcast announcing that matchmaking paired a room session and match execution is commencing.
     * <p>
     * <b>Format:</b> {@code GAME_STARTED|player1Name|player2Name}
     */
    GAME_STARTED,

    /**
     * Server sync broadcast announcing that a specific player has assumed turn clearances and active control options.
     * <p>
     * <b>Format:</b> {@code TURN_STARTED|activePlayerName}
     */
    TURN_STARTED,

    /**
     * Server replication event notifying participants that a specific unit changed structural grid tile positions.
     * <p>
     * <b>Format:</b> {@code UNIT_MOVED|unitId|x|y}
     */
    UNIT_MOVED,

    /**
     * Server replication event confirming an attack engagement and broadcasting updated remaining health parameters of the defender.
     * <p>
     * <b>Format:</b> {@code UNIT_ATTACKED|attackerId|targetId|newRemainingHp}
     */
    UNIT_ATTACKED,

    /**
     * Server replication event notifying clients that a combat unit sustained fatal damage and must be purged from map displays.
     * <p>
     * <b>Format:</b> {@code UNIT_DIED|deadUnitId}
     */
    UNIT_DIED,

    /**
     * Server replication event notifying clients that a participant successfully recruited a troop containing an allocated tracking key.
     * <p>
     * <b>Format:</b> {@code UNIT_BOUGHT|cityId|newUnitId|troopTypeClass}
     */
    UNIT_BOUGHT,

    /**
     * Server replication event confirming that a settlement structure expanded its structural tier ranking value.
     * <p>
     * <b>Format:</b> {@code CITY_UPGRADED|cityId}
     */
    CITY_UPGRADED,

    /**
     * Server replication event updating resource balance metrics for individual target player profiles.
     * <p>
     * <b>Format:</b> {@code GOLD_UPDATED|playerName|newBalanceTotal}
     */
    GOLD_UPDATED,

    /**
     * Server broadcast declaring that final victory condition thresholds were met and an absolute winner has emerged.
     * <p>
     * <b>Format:</b> {@code GAME_OVER|winningPlayerName}
     */
    GAME_OVER
}