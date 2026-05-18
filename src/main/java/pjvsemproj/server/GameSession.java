package pjvsemproj.server;

import com.google.gson.Gson;
import pjvsemproj.dto.*;
import pjvsemproj.models.services.CoreGameService;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * Represents a multiplayer game session between two players.
 * <p>
 * Coordinates game logic and synchronizes state between clients. It mirrors
 * operational requests to the core service referee engine, intercepts rejections, and broadcasts
 * validated state mutations to both player participants using the networking protocols.
 */
public class GameSession {
    private static final Logger LOGGER = Logger.getLogger(GameSession.class.getName());

    private final GameServer gameServer;
    private final Connection connection1;
    private final Connection connection2;
    private final CoreGameService gameService;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean gameStarted = false;

    /**
     * Constructs an isolated matchmaking session room pairing two network workers.
     *
     * @param gameServer  The master background game server state manager handling execution lifecycles.
     * @param c1          The network connection handler thread allocated for the first participant player.
     * @param c2          The network connection handler thread allocated for the second participant player.
     * @param gameService The authoritative core referee logic processing engine bound to this session.
     */
    public GameSession(GameServer gameServer, Connection c1, Connection c2, CoreGameService gameService) {
        this.gameServer = gameServer;
        this.connection1 = c1;
        this.connection2 = c2;
        this.gameService = gameService;
    }

    /**
     * Commands the match session to initialize, converts map models to JSON, and sends data packages across network sockets.
     * <p>
     * Sends initial protocol markers containing usernames, compiles the base game DTO map layout,
     * encodes the state structure using GSON utilities, and dispatches the data payload to both clients.
     */
    public void startGame() {
        if (gameStarted) {
            return;
        }

        gameStarted = true;

        List<PlayerDTO> players = gameService.getPlayersDTO();
        String p1Name = players.get(0).name;
        String p2Name = players.get(1).name;

        connection1.sendToClient(Protocol.GAME_STARTED, p1Name, p2Name);
        connection2.sendToClient(Protocol.GAME_STARTED, p1Name, p2Name);

        GameDTO gameDTO = gameService.getGameDTO();

        Gson gson = new Gson();
        String json = gson.toJson(gameDTO);

        connection1.sendToClient(Protocol.GAME_STATE, json);
        connection2.sendToClient(Protocol.GAME_STATE, json);
    }

    /**
     * Registers readiness tokens from incoming player packages and checks if the session can boot.
     *
     * @param connection The specific client {@link Connection} thread worker claiming readiness.
     */
    public synchronized void handleReady(Connection connection) {
        if (gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_ALREADY_STARTED");
            return;
        }

        if (connection == connection1) {
            player1Ready = true;
            connection1.sendToClient(Protocol.OK, "READY_ACCEPTED");
        } else if (connection == connection2) {
            player2Ready = true;
            connection2.sendToClient(Protocol.OK, "READY_ACCEPTED");
        }

        if (player1Ready && player2Ready) {
            startGame();
        }
    }

    /**
     * Intercepts, validates, and replicates a unit navigation move instruction across the active network.
     *
     * @param connection The client connection thread attempting to move an asset.
     * @param unitId     Unique identifier tracking token matching the target mobile troop.
     * @param x          The vertical column coordinate destination index.
     * @param y          The horizontal row coordinate destination index.
     */
    public synchronized void onMove(Connection connection, String unitId, int x, int y) {
        if (!validateActionAllowed(connection)) return;

        try {
            if (!gameService.moveUnit(unitId, x, y)) {
                connection.sendToClient(Protocol.ERROR, "MOVE_FAILED");
                return;
            }
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        broadcast(Protocol.UNIT_MOVED, unitId, String.valueOf(x), String.valueOf(y));
    }

    /**
     * Processes, executes, and replicates an offensive combat engagement across the active session.
     * <p>
     * Validates turn clearances, resolves local damage range calculations, extracts remaining defender metrics,
     * and broadcasts updated status parameters or health updates to both nodes.
     *
     * @param connection The client connection thread instigating the combat interaction.
     * @param attackerId Unique tracking key matching the attacking offensive unit.
     * @param targetId   Unique tracking key matching the defending target unit.
     */
    public synchronized void onAttack(Connection connection, String attackerId, String targetId) {
        if (!validateActionAllowed(connection)) return;

        try {
            if (!gameService.attack(attackerId, targetId)) {
                connection.sendToClient(Protocol.ERROR, "ATTACK_FAILED");
                return;
            }
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        TroopUnitDTO target = (TroopUnitDTO) gameService.getEntityDTO(targetId);
        String hp = (target == null) ? "0" : String.valueOf(target.hp);

        broadcast(Protocol.UNIT_ATTACKED, attackerId, targetId, hp);
    }

    /**
     * Processes financial transactions and updates unit recruitment states following a purchase request.
     * <p>
     * Verifies game authorization rules, performs economic gold balance deductions, sweeps the production grid tiles
     * to isolate the newly initialized troop DTO container, and broadcasts production updates to both clients.
     *
     * @param connection The client connection thread initiating the unit purchase.
     * @param cityId     Unique tracking code matching the factory settlement creating the asset.
     * @param troopType  Class metadata description matching the target unit type config.
     */
    public synchronized void onUnitPurchase(Connection connection, String cityId, String troopType) {
        if (!validateActionAllowed(connection)) return;

        try {
            if (!gameService.buyUnit(cityId, troopType)) {
                connection.sendToClient(Protocol.ERROR, "ERROR_BUYING_TROOP");
                return;
            }
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        CityDTO city = (CityDTO) gameService.getEntityDTO(cityId);
        TileDTO tile = gameService.getTileDTO(city.x, city.y);

        TroopUnitDTO boughtUnit = null;

        for (EntityDTO entity : tile.entities) {
            if (entity instanceof TroopUnitDTO troop) {
                if (currentPlayer.name.equals(troop.ownerName)
                        && troopType.equals(troop.entityType)
                        && troop.hasMovedThisTurn
                        && troop.hasAttackedThisTurn) {
                    boughtUnit = troop;
                }
            }
        }

        if (boughtUnit == null) {
            connection.sendToClient(Protocol.ERROR, "BOUGHT_UNIT_NOT_FOUND");
            return;
        }

        broadcast(Protocol.UNIT_BOUGHT, cityId, boughtUnit.id, boughtUnit.entityType);
    }

    /**
     * Processes settlement tier advancements and broadcasts structural upgrades across the session.
     *
     * @param connection The client connection thread requesting the tier modification.
     * @param cityId     Unique identification lookup token of the city structure targeted for upgrade.
     */
    public synchronized void onCityUpgrade(Connection connection, String cityId) {
        if (!validateActionAllowed(connection)) return;

        try {
            if (!gameService.upgradeCity(cityId)) {
                connection.sendToClient(Protocol.ERROR, "UPGRADE_CITY_FAILURE");
                return;
            }
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        broadcast(Protocol.CITY_UPGRADED, cityId);
    }

    /**
     * Executes turn rotation commands, moves control access flags, and notifies both clients.
     *
     * @param connection The client connection thread ending its turn option block.
     */
    public synchronized void onEndTurn(Connection connection) {
        if (!validateActionAllowed(connection)) return;

        gameService.endTurn();
        PlayerDTO newCurrentPlayer = gameService.getCurrentPlayerDTO();

        broadcast(Protocol.TURN_STARTED, newCurrentPlayer.name);
    }

    /**
     * Enforces core security and rules verification checks on incoming client packages.
     * <p>
     * Verifies that the match is active and confirms that the sending client's registered name
     * matches the current turn clearance token managed by the engine.
     *
     * @param connection The incoming {@link Connection} node requesting changes.
     * @return {@code true} if the client holds valid input clearances; {@code false} if unauthorized.
     */
    private boolean validateActionAllowed(Connection connection) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return false;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return false;
        }

        return true;
    }

    /**
     * Broadcasts an encoded message packet to all connected clients in the session.
     *
     * @param protocol The target communication {@link Protocol} header token that classifies the package action context.
     * @param data     Varargs string parameters to append behind the message header.
     */
    private void broadcast(Protocol protocol, String... data) {
        connection1.sendToClient(protocol, data);
        connection2.sendToClient(protocol, data);
    }

    /**
     * Handles unexpected player disconnects or active surrender packages during live sessions.
     *
     * @param connection The specific {@link Connection} that requested disconnection.
     */
    public synchronized void onPlayerQuit(Connection connection) {
        LOGGER.info("Server sending GAME_OVER to remaining player");
        endGameForRemainingPlayer(connection);
    }

    /**
     * Forces standard connection closures across all bounded client threads inside this room.
     */
    public synchronized void terminate() {
        if (connection1 != null) connection1.closeConnection();
        if (connection2 != null) connection2.closeConnection();
    }

    /**
     * Processes early game resolution when a player leaves, declaring the remaining player the absolute winner.
     * <p>
     * Isolates the remaining stream channel, dispatches authoritative victory code markers,
     * purges the room from the server tracking list, and closes socket allocations gracefully.
     *
     * @param connectionThatLeft The specific network connection that disconnected.
     */
    private void endGameForRemainingPlayer(Connection connectionThatLeft) {
        Connection remainingConnection = (connectionThatLeft == connection1) ? connection2 : connection1;

        if (remainingConnection != null) {
            String winnerName = remainingConnection.getPlayerName();
            remainingConnection.sendToClient(Protocol.GAME_OVER, winnerName);
        }

        gameServer.removeSession(this);
        gameServer.stopServer();
    }

    /**
     * Fetches the connection thread wrapper managing communications for player participant 1.
     *
     * @return Connection thread instance.
     */
    public Connection getConnection1() {
        return connection1;
    }

    /**
     * Fetches the connection thread wrapper managing communications for player participant 2.
     *
     * @return Connection thread instance.
     */
    public Connection getConnection2() {
        return connection2;
    }
}