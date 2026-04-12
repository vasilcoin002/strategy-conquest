package pjvsemproj.server;

import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

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

    public GameSession(GameServer gameServer, Connection connection1, Connection connection2, GameMap map, Game game) {
        this.gameServer = gameServer;
        this.connection1 = connection1;
        this.connection2 = connection2;

        this.player1 = connection1.getPlayer();
        this.player2 = connection2.getPlayer();
        this.game = game;
        this.map = game.getMap();
        this.movementManager = new MovementManager(this.map);


        this.combatManager = new CombatManager();
        this.economyManager = new EconomyManager();
        this.turnManager = new TurnManager(player1, player2);

        this.turnManager.addTurnListener(movementManager);
        this.turnManager.addTurnListener(combatManager);
        this.turnManager.addTurnListener(economyManager);

    }

    public void startGame(){}
    public synchronized void handleReady(Connection connection){}
    public synchronized void onMove(Connection connection, String unitId, int x, int y){}
    public synchronized void onAttack(Connection connection, String attackerId, String targetId){}
    public synchronized void onUnitPurchase(Connection connection, String cityId, String troopType){}
    public synchronized void onCityUpgrade(Connection connection, String cityId){}
    public synchronized void onEndTurn(Connection connection){}
    public synchronized void onPlayerQuit(Connection connection){}
    public synchronized void onPlayerDisconnect(Connection connection){}



}
