package pjvsemproj.models.services;

import pjvsemproj.models.entities.cities.City;
import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.Game;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.models.managers.utils.GridPositionHelper;
import pjvsemproj.models.managers.utils.OwnershipHelper;
import pjvsemproj.server.Client;
import pjvsemproj.server.NetworkGameListener;
import pjvsemproj.server.Protocol;
import pjvsemproj.server.ServerEventListener;

// TODO change methods which send data to server to also check server response to change local state
//  for example moveUnit(x, y) should check if there is a positive response from server before moving it locally
/**
 * Network-based implementation of GameService.
 * Sends commands to the server instead of executing them locally.
 */
public class NetworkGameService extends AbstractClientService {

    private final Client client;

    public NetworkGameService(Client client, Game game) {
        super(game);
        this.client = client;

        client.setServerEventListener(new NetworkGameListener(this) {
        });
    }

    @Override
    public void login(String playerName) {
        client.sendToServer(Protocol.LOGIN, playerName);
        super.login(playerName);
    }

    @Override
    public void ready() {
        client.ready();
        super.ready();
    }

    @Override
    public boolean moveUnit(String unitId, int x, int y) {
        client.moveUnit(unitId, x, y);
        super.moveUnit(unitId, x, y);
        return false;
    }

    @Override
    public boolean attack(String attackerId, String targetId) {
        client.attack(attackerId, targetId);
        super.attack(attackerId, targetId);
        return false;
    }

    @Override
    public boolean buyUnit(String cityId, String troopType) {
        client.buyUnit(cityId, troopType);
        super.buyUnit(cityId, troopType);
        return false;
    }

    @Override
    public boolean upgradeCity(String cityId) {
        client.upgradeCity(cityId);
        super.upgradeCity(cityId);
        return false;
    }

    @Override
    public void endTurn() {
        client.endTurn();
        super.endTurn();
    }

    @Override
    public void quit() {
        client.quit();
        super.quit();
    }

    public void applyServerMove(String unitId, int x, int y) {
        boolean success = super.moveUnit(unitId, x, y);

        if (success) {
            notifyBoardUpdated();
        }
    }

    public void applyServerAttack(String attackerId, String targetId, int newHp){
        TroopUnit target = findTroopById(targetId);
        target.setHealth(newHp);

        TroopUnit attacker = findTroopById(attackerId);
        attacker.setHasAttackedThisTurn(true);
        attacker.setHasMovedThisTurn(true);

        notifyBoardUpdated();
    }

    public void applyServerUnitDeath(String unitId) {
        TroopUnit troop = findTroopById(unitId);

        OwnershipHelper.removeTroopUnitFromPlayer(troop);
        GridPositionHelper.removeFromBoard(troop);

        notifyBoardUpdated();
    }

    public void applyServerTurnStarted(String playerName){
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(playerName)) {
                game.setCurrentPlayer(player);
                notifyBoardUpdated();
                return;
            }
        }
    }

    public void applyServerCityUpgrade(String cityId, String newLevel) {
        City city = findCityById(cityId);

        while (!city.getCityType().name().equals(newLevel) && city.canBeUpgraded()) {
            city.upgrade();
        }

        notifyBoardUpdated();
    }
}