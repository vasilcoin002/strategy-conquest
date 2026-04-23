package pjvsemproj.models.services;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopType;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.CombatManager;
import pjvsemproj.models.managers.EconomyManager;
import pjvsemproj.models.managers.MovementManager;
import pjvsemproj.models.managers.TurnManager;

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


    // TODO save game
    @Override
    public void quit() {
        System.out.println("Game quit");
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
