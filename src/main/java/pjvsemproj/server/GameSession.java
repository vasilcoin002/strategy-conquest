package pjvsemproj.server;

import com.google.gson.Gson;
import pjvsemproj.dto.*;
import pjvsemproj.models.services.CoreGameService;

import java.util.List;
import java.util.Objects;


/**
 * Represents a multiplayer game session between two players.
 * <p>
 * Coordinates game logic and synchronizes state between clients.
 */
public class GameSession {

    private final GameServer gameServer;
    private final Connection connection1;
    private final Connection connection2;

    private final CoreGameService gameService;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean gameStarted = false;

    public GameSession(GameServer gameServer, Connection c1, Connection c2, CoreGameService gameService) {
        this.gameServer = gameServer;
        this.connection1 = c1;
        this.connection2 = c2;

        this.gameService = gameService;
    }

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

    public synchronized void onEndTurn(Connection connection) {
        if (!validateActionAllowed(connection)) return;

        gameService.endTurn();
        PlayerDTO newCurrentPlayer = gameService.getCurrentPlayerDTO();

        broadcast(Protocol.TURN_STARTED, newCurrentPlayer.name);
    }

    /**
     * Helper to verify if the game is running and if it is the requesting player's turn.
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
     * Helper to broadcast a protocol message to all connected clients.
     */
    private void broadcast(Protocol protocol, String... data) {
        connection1.sendToClient(protocol, data);
        connection2.sendToClient(protocol, data);
    }

    public synchronized void onPlayerQuit(Connection connection) {
        System.out.println("Server sending GAME_OVER to remaining player");
        endGameForRemainingPlayer(connection);
    }

    public synchronized void terminate() {
        if (connection1 != null) connection1.closeConnection();
        if (connection2 != null) connection2.closeConnection();
    }

    private void endGameForRemainingPlayer(Connection connectionThatLeft) {
        Connection remainingConnection = (connectionThatLeft == connection1) ? connection2 : connection1;

        if (remainingConnection != null) {
            String winnerName = remainingConnection.getPlayerName();
            remainingConnection.sendToClient(Protocol.GAME_OVER, winnerName);
        }

        gameServer.removeSession(this);
        gameServer.stopServer();
    }

    public Connection getConnection1() {
        return connection1;
    }

    public Connection getConnection2() {
        return connection2;
    }
}
