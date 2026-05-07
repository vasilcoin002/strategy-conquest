package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.server.Client;
import pjvsemproj.server.Protocol;

// TODO change methods which send data to server to also check server response to change local state
//  for example moveUnit(x, y) should check if there is a positive response from server before moving it locally
/**
 * Network-based implementation of GameService.
 *
 * Sends commands to the server instead of executing them locally.
 */
public class NetworkGameService extends AbstractClientService {

    private final Client client;

    public NetworkGameService(Client client, Game game) {
        super(game);
        this.client = client;
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
}
