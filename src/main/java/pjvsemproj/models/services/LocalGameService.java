package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

import java.util.List;
import java.util.Set;

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

        this.turnManager = new TurnManager(player1, player2);

        this.movementManager = new MovementManager(map);
        this.combatManager = new CombatManager(map, turnManager.getCurrentPlayer());
        this.economyManager = new EconomyManager(turnManager.getCurrentPlayer());

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
    public void moveTroopUnit(String unitId, int x, int y) {

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

    public void saveGame() {

    }

    @Override
    public void quit() {

    }

    @Override
    public GameMap getMap() {
        return null;
    }

    @Override
    public List<Player> getPlayers() {
        return List.of();
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public Set<Tile> getAvailableTilesForMovement(String unitId) {
        return Set.of();
    }
}
