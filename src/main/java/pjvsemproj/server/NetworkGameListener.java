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
        service.applyServerTurnStarted(playerName);
    }

    @Override
    public void onCityUpgraded(String cityId, String newLevel) {
        service.applyServerCityUpgrade(cityId, newLevel);
    }

    @Override
    public void onUnitBought(
            String cityId,
            String unitId,
            String troopType,
            int x,
            int y,
            String ownerName,
            int newBalance
    ) {
        service.applyServerUnitBought(
                cityId,
                unitId,
                troopType,
                x,
                y,
                ownerName,
                newBalance
        );
    }
}
