package pjvsemproj.models.services;

public class NetworkGameService implements GameService {

    private final Client client;
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
}
