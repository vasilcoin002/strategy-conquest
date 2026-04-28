package pjvsemproj.models.services;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.*;

import java.util.List;
import java.util.Set;



/**
 * Local (single-player) implementation of GameService.
 *
 * Directly interacts with managers to execute game actions.
 */
public class LocalGameService implements GameService {
    private final Game game;
    private final GameMap map;

    private final MovementManager movementManager;
    private final CombatManager combatManager;
    private final EconomyManager economyManager;
    private final TurnManager turnManager;
    private final ConquestManager conquestManager;


    public LocalGameService(Game game) {
        this.game = game;
        this.map = game.getMap();

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        this.turnManager = new TurnManager(player1, player2);

        this.movementManager = new MovementManager(this.map, turnManager.getCurrentPlayer());
        this.combatManager = new CombatManager(this.map, turnManager.getCurrentPlayer());
        this.economyManager = new EconomyManager(turnManager.getCurrentPlayer());
        this.conquestManager = new ConquestManager(game.getPlayers(), turnManager.getCurrentPlayer());

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
        TroopUnit troopUnit = findTroopById(unitId);
        if(troopUnit == null){
            return;
        }
        Tile tile = game.getMap().getTile(x, y);
        Set<Tile> availableTiles = movementManager.getAvailableTilesForMovement(troopUnit);

        if(!availableTiles.contains(tile)){
            return;
        }
        movementManager.moveTroopUnit(troopUnit, tile);
        if (conquestManager.checkIfWinnerExists()) {
            conquestManager.announceWinner(troopUnit.getOwner());
        }
    }

    @Override
    public void attack(String attackerId, String targetId) {
        TroopUnit attacker = findTroopById(attackerId);
        TroopUnit target = findTroopById(targetId);
        if(attacker == null || target == null){
            return;
        }
        Set<TroopUnit> attackableTroops = combatManager.getAttackableTroops(attacker);

        if(!attackableTroops.contains(target)){
            return;
        }
        combatManager.attackTroop(attacker, target);
    }

    @Override
    public void buyUnit(String cityId, String troopType) {
        City city = findCityById(cityId);
        if(city == null){
            return;
        }

        TroopType type = TroopType.valueOf(troopType);
        economyManager.buyTroopUnit(type, city);
    }

    @Override
    public void upgradeCity(String cityId) {
        City city = findCityById(cityId);
        if(city == null){
            return;
        }

        economyManager.upgradeCity(city);
    }

    @Override
    public void endTurn() {
        turnManager.endTurn();
    }

    public void saveGame() {

    }

    public void addWinListener(IWinListener listener) {
        conquestManager.addWinListener(listener);
    }


    @Override
    public void quit() {
        System.out.println("Game quit");
    }



    @Override
    public GameMap getMap() {
        return map;
    }

    @Override
    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    @Override
    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    @Override
    public Set<Tile> getAvailableTilesForMovement(String unitId) {
        return movementManager.getAvailableTilesForMovement(
                findTroopById(unitId)
        );
    }

    private TroopUnit findTroopById(String id){
        for (Player player: game.getPlayers()) {
            for(TroopUnit troopUnit: player.getTroops()){
                if(troopUnit.getId().equals(id)){
                    return troopUnit;
                }
            }
        }
        return null;
    }

    private City findCityById(String id){
        for(Player player: game.getPlayers()){
            for(City city: player.getCities()){
                if(city.getId().equals(id)){
                    return city;
                }
            }
        }
        return null;
    }
}
