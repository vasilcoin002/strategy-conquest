package pjvsemproj.server;

import pjvsemproj.models.services.NetworkGameService;

public class NetworkGameListener implements ServerEventListener {

    private final NetworkGameService service;

    public NetworkGameListener(NetworkGameService service) {
        this.service = service;
    }

    @Override
    public void onUnitMoved(String unitId, int x, int y) {
    }

    @Override
    public void onUnitAttacked(String attackerId, String targetId, int newHp) {

    }

    @Override
    public void onUnitDied(String unitId) {

    }

    @Override
    public void onTurnStarted(String playerName) {

    }

    @Override
    public void onCityUpgraded(String cityId) {

    }
}
