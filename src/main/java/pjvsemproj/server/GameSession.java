package pjvsemproj.server;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;


/**
 * Represents a multiplayer game session between two players.
 *
 * Coordinates game logic and synchronizes state between clients.
 */
public class GameSession {

    private final GameServer gameServer;
    private final Connection connection1;
    private final Connection connection2;

    private final Player player1;
    private final Player player2;
    private final Game game;

    private final GameMap map;
    private final MovementManager movementManager;
    private final CombatManager combatManager;
    private final EconomyManager economyManager;
    private final TurnManager turnManager;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean gameStarted = false;

    public GameSession(GameServer gameServer, Connection connection1, Connection connection2, GameMap map, Game game) {
        this.gameServer = gameServer;
        this.connection1 = connection1;
        this.connection2 = connection2;

        this.player1 = connection1.getPlayer();
        this.player2 = connection2.getPlayer();
        this.game = game;
        this.map = game.getMap();

        this.turnManager = new TurnManager(player1, player2);

        this.movementManager = new MovementManager(this.map, turnManager.getCurrentPlayer());
        this.combatManager = new CombatManager(map, turnManager.getCurrentPlayer());
        this.economyManager = new EconomyManager(turnManager.getCurrentPlayer());

        this.turnManager.addTurnListener(movementManager);
        this.turnManager.addTurnListener(combatManager);
        this.turnManager.addTurnListener(economyManager);
    }

    public void startGame(){
        if(gameStarted){
            return;
        }

        gameStarted = true;
        connection1.sendToClient(Protocol.GAME_STARTED,player1.getName(), player2.getName());
        connection2.sendToClient(Protocol.GAME_STARTED, player1.getName(), player2.getName());

        Player currentPlayer = turnManager.getCurrentPlayer();

        connection1.sendToClient(Protocol.TURN_STARTED, currentPlayer.getName());
        connection2.sendToClient(Protocol.TURN_STARTED, currentPlayer.getName());

        turnManager.startTurn(currentPlayer);
    }

    public synchronized void handleReady(Connection connection){
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

    public synchronized void onMove(Connection connection, String unitId, int x, int y){
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        Player currentPlayer = turnManager.getCurrentPlayer();

        if (connection.getPlayer() != currentPlayer) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }
        TroopUnit unit = findUnitById(unitId);

        if (unit == null) {
            connection.sendToClient(Protocol.ERROR, "UNIT_NOT_FOUND");
            return;
        }
        Tile targetTile = map.getTile(x, y);
        boolean success = movementManager.moveTroopUnit(unit, targetTile);

        if (!success) {
            connection.sendToClient(Protocol.ERROR, "MOVE_FAILED");
            return;
        }

        connection1.sendToClient(Protocol.UNIT_MOVED, unitId, String.valueOf(x), String.valueOf(y));
        connection2.sendToClient(Protocol.UNIT_MOVED, unitId, String.valueOf(x), String.valueOf(y));
    }

    public synchronized void onAttack(Connection connection, String attackerId, String targetId){
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        Player currentPlayer = turnManager.getCurrentPlayer();
        if (connection.getPlayer() != currentPlayer) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }
        TroopUnit attacker = findUnitById(attackerId);
        if (attacker == null) {
            connection.sendToClient(Protocol.ERROR, "UNIT_NOT_FOUND");
            return;
        }
        TroopUnit target = findUnitById(targetId);
        if(target == null){
            connection.sendToClient(Protocol.ERROR, "TARGET_NOT_FOUND");
        }
        boolean success = combatManager.attackTroop(attacker, target);
        if (!success) {
            connection.sendToClient(Protocol.ERROR, "ATTACK_FAILED");
            return;
        }

        connection1.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.getHealth()));
        connection2.sendToClient(Protocol.UNIT_ATTACKED, attackerId, targetId, String.valueOf(target.getHealth()));

        if(target.isDead()){
            connection1.sendToClient(Protocol.UNIT_DIED, targetId);
            connection2.sendToClient(Protocol.UNIT_DIED, targetId);
        }
    }

    public synchronized void onUnitPurchase(Connection connection, String cityId, String troopType){
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        Player currentPlayer = turnManager.getCurrentPlayer();
        if (connection.getPlayer() != currentPlayer) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        City city = findCityById(cityId);
        if(city == null){
            connection.sendToClient(Protocol.ERROR, "CITY_NOT_FOUND");
            return;
        }

        boolean success = economyManager.buyTroopUnit(TroopType.valueOf(troopType), city);
        if(!success){
            connection.sendToClient(Protocol.ERROR, "ERROR_BUYING_TROOP");
            return;
        }
        connection1.sendToClient(Protocol.BUY_UNIT, String.valueOf(TroopType.valueOf(troopType)));
        connection2.sendToClient(Protocol.UNIT_BOUGHT, String.valueOf(TroopType.valueOf(troopType)));
    }
    public synchronized void onCityUpgrade(Connection connection, String cityId){
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        Player currentPlayer = turnManager.getCurrentPlayer();
        if (connection.getPlayer() != currentPlayer) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }

        City city = findCityById(cityId);
        if(city == null){
            connection.sendToClient(Protocol.ERROR, "CITY_NOT_FOUND");
            return;
        }

        boolean success = economyManager.upgradeCity(city);
        if(!success){
            connection.sendToClient(Protocol.ERROR, "UPGRADE_CITY_FAILURE");
            return;
        }
        connection1.sendToClient(Protocol.UPGRADE_CITY, cityId, city.getCityType().name(), String.valueOf(currentPlayer.getBalance()));
        connection2.sendToClient(Protocol.CITY_UPGRADED, cityId, city.getCityType().name());
    }

    public synchronized void onEndTurn(Connection connection){
        if (!gameStarted) {
            connection.sendToClient(Protocol.ERROR, "GAME_NOT_STARTED");
            return;
        }

        Player currentPlayer = turnManager.getCurrentPlayer();
        if (connection.getPlayer() != currentPlayer) {
            connection.sendToClient(Protocol.ERROR, "NOT_YOUR_TURN");
            return;
        }
        turnManager.endTurn();
        Player newCurrentPlayer = turnManager.getCurrentPlayer();

        connection1.sendToClient(Protocol.TURN_STARTED, newCurrentPlayer.getName());
        connection2.sendToClient(Protocol.TURN_STARTED, newCurrentPlayer.getName());
    }
    public synchronized void onPlayerQuit(Connection connection){
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

    public synchronized void onPlayerDisconnect(Connection connection){
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

    private TroopUnit findUnitById(String unitId){
        for(Player player: game.getPlayers()){
            for(TroopUnit troopUnit: player.getTroops()){
                if(troopUnit.getId().equals(unitId)){
                    return troopUnit;
                }
            }
        }
        return null;
    }

    private City findCityById(String cityId){
        for(Player player: game.getPlayers()){
            for(City city: player.getCities()){
                if(city.getId().equals(cityId)){
                    return city;
                }
            }
        }
        return null;
    }

    public Connection getConnection1(){
        return connection1;
    }

    public Connection getConnection2(){
        return connection2;
    }
}
