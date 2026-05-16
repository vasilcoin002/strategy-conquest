package pjvsemproj.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pjvsemproj.config.EntityDTODeserializer;
import pjvsemproj.dto.*;
import pjvsemproj.models.entities.troopUnits.TroopType;
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

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();


        // not calling startFirstTurn to leave game like in configuration
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

        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.moveUnit(unitId, x, y);
        } catch (Exception e) {
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
        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.attack(attackerId, targetId);
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "ATTACK_FAILED");
            return;
        }

        TroopUnitDTO target = (TroopUnitDTO) gameService.getEntityDTO(targetId);
        if (target == null) {
            connection1.sendToClient(Protocol.UNIT_DIED, targetId);
            connection2.sendToClient(Protocol.UNIT_DIED, targetId);
            return;
        }

        connection1.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.hp));
        connection2.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.hp));
    }

    public synchronized void onUnitPurchase(Connection connection, String cityId, String troopType) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();

        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;

        try {
            success = gameService.buyUnit(cityId, troopType);
        } catch (Exception e) {
            connection.sendToClient(Protocol.ERROR, e.getMessage());
            return;
        }

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "ERROR_BUYING_TROOP");
            return;
        }

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

        connection1.sendToClient(
                Protocol.UNIT_BOUGHT,
                cityId,
                boughtUnit.id,
                boughtUnit.entityType
        );

        connection2.sendToClient(
                Protocol.UNIT_BOUGHT,
                cityId,
                boughtUnit.id,
                boughtUnit.entityType
        );
    }

    public synchronized void onCityUpgrade(Connection connection, String cityId) {
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        PlayerDTO currentPlayer = gameService.getCurrentPlayerDTO();
        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        boolean success;
        try {
            success = gameService.upgradeCity(cityId);
        } catch (Exception e) {
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
        if (!Objects.equals(connection.getPlayerName(), currentPlayer.name)) {
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
        String disconnectedPlayerName = connection.getPlayerName();
        Connection otherConnection;

        if (connection == connection1) {
            otherConnection = connection2;
        } else {
            otherConnection = connection1;
        }
        if (otherConnection != null) {
            otherConnection.sendToClient(
                    Protocol.QUIT,
                    disconnectedPlayerName != null ? disconnectedPlayerName : "UNKNOWN"
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
