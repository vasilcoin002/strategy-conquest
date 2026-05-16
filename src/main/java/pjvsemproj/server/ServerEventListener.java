package pjvsemproj.server;

public interface ServerEventListener {

    void onUnitMoved(String unitId, int x, int y);

    void onUnitAttacked(
            String attackerId,
            String targetId,
            int newHp
    );

    void onUnitDied(String unitId);

    void onTurnStarted(String playerName);

    void onCityUpgraded(String cityId, String newLevel);

    void onUnitBought(
            String cityId,
            String unitId,
            String troopType
    );
}
