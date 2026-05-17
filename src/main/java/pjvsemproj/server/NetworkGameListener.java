package pjvsemproj.server;

import pjvsemproj.models.services.NetworkGameService;

public class NetworkGameListener implements ServerEventListener {

    private final NetworkGameService service;

    public NetworkGameListener(NetworkGameService service) {
        this.service = service;
    }

    @Override
    public void onUnitMoved(String unitId, int x, int y) {
        service.applyServerMove(unitId, x, y);
    }

    @Override
    public void onUnitAttacked(String attackerId, String targetId, int newHp) {
        service.applyServerAttack(attackerId, targetId, newHp);
    }

    @Override
    public void onUnitDied(String unitId) {
        service.applyServerUnitDeath(unitId);
    }

    @Override
    public void onTurnStarted(String playerName) {
        service.applyServerTurnStarted();
    }

    @Override
    public void onCityUpgraded(String cityId) {
        service.applyServerCityUpgrade(cityId);
    }

    @Override
    public void onUnitBought(
            String cityId,
            String unitId,
            String troopType
    ) {
        service.applyServerUnitBought(
                cityId,
                unitId,
                troopType
        );
    }

    @Override
    public void onGameOver(String winnerName) {
        service.applyServerGameOver(winnerName);
    }
}
