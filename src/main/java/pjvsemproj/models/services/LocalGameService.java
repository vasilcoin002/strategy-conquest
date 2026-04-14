package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

public class LocalGameService implements GameService {
    private final Game game;
    private final GameMap map;

    private final MovementManager movementManager;
    private final CombatManager combatManager;
    private final EconomyManager economyManager;
    private final TurnManager turnManager;


    public LocalGameService(Game game) {
        this.game = game;
        this.map = game.getMap();

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        this.movementManager = new MovementManager(this.map);


        this.combatManager = new CombatManager();
        this.economyManager = new EconomyManager();
        this.turnManager = new TurnManager(player1, player2);

        this.turnManager.addTurnListener(movementManager);
        this.turnManager.addTurnListener(combatManager);
        this.turnManager.addTurnListener(economyManager);
    }

    @Override
    public void login(String playerName) {

    }

    @Override
    public void ready() {

    }

    @Override
    public void moveUnit(String unitId, int x, int y) {

    }

    @Override
    public void attack(String attackerId, String targetId) {

    }

    @Override
    public void buyUnit(String cityId, String troopType) {

    }

    @Override
    public void upgradeCity(String cityId) {

    }

    @Override
    public void endTurn() {

    }

    @Override
    public void quit() {

    }
}
