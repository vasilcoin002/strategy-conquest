package pjvsemproj.models.services;

import pjvsemproj.models.game.Game;
import pjvsemproj.server.Client;
import pjvsemproj.server.Protocol;

// TODO implement methods which send data to server to also change local state
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
    public void moveUnit(String unitId, int x, int y) {
        client.moveUnit(unitId, x, y);
        super.moveUnit(unitId, x, y);
    }

    @Override
    public void attack(String attackerId, String targetId) {
        client.attack(attackerId, targetId);
        super.attack(attackerId, targetId);
    }

    @Override
    public void buyUnit(String cityId, String troopType) {
        client.buyUnit(cityId, troopType);
        super.buyUnit(cityId, troopType);
    }

    @Override
    public void upgradeCity(String cityId) {
        client.upgradeCity(cityId);
        super.upgradeCity(cityId);
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
