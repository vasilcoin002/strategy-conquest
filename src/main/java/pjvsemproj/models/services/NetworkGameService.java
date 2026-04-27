package pjvsemproj.models.services;

import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.server.Client;
import pjvsemproj.server.Protocol;

import java.util.List;
import java.util.Set;


/**
 * Network-based implementation of GameService.
 *
 * Sends commands to the server instead of executing them locally.
 */
public class NetworkGameService implements GameService {

    private final Client client;

    public NetworkGameService(Client client) {
        this.client = client;
    }

    @Override
    public void login(String playerName) {
        client.sendToServer(Protocol.LOGIN, playerName);
    }

    @Override
    public void ready() {
        client.ready();
    }

    @Override
    public void moveUnit(String unitId, int x, int y) {
        client.moveUnit(unitId, x, y);
    }

    @Override
    public void attack(String attackerId, String targetId) {
        client.attack(attackerId, targetId);
    }

    @Override
    public void buyUnit(String cityId, String troopType) {
        client.buyUnit(cityId, troopType);
    }

    @Override
    public void upgradeCity(String cityId) {
        client.upgradeCity(cityId);
    }

    @Override
    public void endTurn() {
        client.endTurn();
    }

    @Override
    public void quit() {
        client.quit();

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
