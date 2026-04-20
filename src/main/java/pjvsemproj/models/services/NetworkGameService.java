package pjvsemproj.models.services;

import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;
import pjvsemproj.server.Client;

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
