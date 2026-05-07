package pjvsemproj.server;

import pjvsemproj.dto.CityDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TroopUnitDTO;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.services.CoreGameService;

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

    private final Player player1;
    private final Player player2;

    private final CoreGameService gameService;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean gameStarted = false;

    public GameSession(GameServer gameServer, Connection c1, Connection c2, CoreGameService gameService) {
        this.gameServer = gameServer;
        this.connection1 = c1;
        this.connection2 = c2;

        this.gameService = gameService;

        this.player1 = connection1.getPlayer();
        this.player2 = connection2.getPlayer();
    }

    public void startGame() {
        if (gameStarted) {
            return;
        }

        gameStarted = true;
        connection1.sendToClient(Protocol.GAME_STARTED, player1.getName(), player2.getName());
        connection2.sendToClient(Protocol.GAME_STARTED, player1.getName(), player2.getName());

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();

        connection1.sendToClient(Protocol.TURN_STARTED, currentPlayer.name);
        connection2.sendToClient(Protocol.TURN_STARTED, currentPlayer.name);

        // TODO think about necessity of the line below
//        turnManager.startTurn(currentPlayer);
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
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();

        if (!Objects.equals(connection.getPlayer().getName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.moveUnit(unitId, x, y);
        } catch (Exception e) {
            // TODO make gameService methods throw exceptions (in this case null pointer exception)
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "MOVE_FAILED");
            return;
        }

        connection1.sendToClient(Protocol.UNIT_MOVED, unitId, String.valueOf(x), String.valueOf(y));
        connection2.sendToClient(Protocol.UNIT_MOVED, unitId, String.valueOf(x), String.valueOf(y));
    }

    public synchronized void onAttack(Connection connection, String attackerId, String targetId) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayer().getName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.attack(attackerId, targetId);
        } catch (Exception e) {
            // TODO make gameService methods throw exceptions (in this case null pointer exception)
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "ATTACK_FAILED");
            return;
        }

        TroopUnitDTO target = (TroopUnitDTO) gameService.getEntityDTO(targetId);


        connection1.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.hp));
        connection2.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.hp));

        if (target.hp == 0) {
            connection1.sendToClient(Protocol.UNIT_DIED, targetId);
            connection2.sendToClient(Protocol.UNIT_DIED, targetId);
        }
    }

    public synchronized void onUnitPurchase(Connection connection, String cityId, String troopType) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayer().getName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.buyUnit(cityId, troopType);
        } catch (Exception e) {
            // TODO make gameService methods throw exceptions (in this case null pointer exception)
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "ERROR_BUYING_TROOP");
            return;
        }
        // TODO change it so both connections receive the same messages
        connection1.sendToClient(Protocol.BUY_UNIT, String.valueOf(TroopType.valueOf(troopType)));
        connection2.sendToClient(Protocol.UNIT_BOUGHT, String.valueOf(TroopType.valueOf(troopType)));
    }

    public synchronized void onCityUpgrade(Connection connection, String cityId) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayer().getName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.upgradeCity(cityId);
        } catch (Exception e) {
            // TODO make gameService methods throw exceptions (in this case null pointer exception)
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "UPGRADE_CITY_FAILURE");
            return;
        }

        CityDTO city = (CityDTO) gameService.getEntityDTO(cityId);

        // TODO change it so both connections receive the same messages
        connection1.sendToClient(Protocol.UPGRADE_CITY, cityId, city.cityLevel, String.valueOf(currentPlayer.balance));
        connection2.sendToClient(Protocol.CITY_UPGRADED, cityId, city.cityLevel);
    }

    public synchronized void onEndTurn(Connection connection) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayer().getName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }
        gameService.endTurn();
        PlayerDTO newCurrentPlayer = gameService.getCurrentPlayerDTO();

        connection1.sendToClient(Protocol.TURN_STARTED, newCurrentPlayer.name);
        connection2.sendToClient(Protocol.TURN_STARTED, newCurrentPlayer.name);
    }

    public synchronized void onPlayerQuit(Connection connection) {
        Connection otherConnection;
        if (connection == connection1) {
            otherConnection = connection2;
        } else {
            otherConnection = connection1;
        }

        if (otherConnection != null) {
            otherConnection.sendToClient(Protocol.QUIT);
        }

        gameServer.removeSession(this);
    }

    public synchronized void onPlayerDisconnect(Connection connection) {
        Player disconnectedPlayer = connection.getPlayer();
        Connection otherConnection;

        if (connection == connection1) {
            otherConnection = connection2;
        } else {
            otherConnection = connection1;
        }
        if (otherConnection != null) {
            otherConnection.sendToClient(
                    Protocol.QUIT,
                    disconnectedPlayer != null ? disconnectedPlayer.getName() : "UNKNOWN"
            );
        }

        gameServer.removeSession(this);
    }

    public Connection getConnection1() {
        return connection1;
    }

    public Connection getConnection2() {
        return connection2;
    }
}
