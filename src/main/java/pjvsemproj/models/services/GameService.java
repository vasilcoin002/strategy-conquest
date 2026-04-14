package pjvsemproj.models.services;

public interface GameService {

    void login(String playerName);

    void ready();

    void moveUnit(String unitId, int x, int y);

    void attack(String attackerId, String targetId);

    void buyUnit(String cityId, String troopType);

    void upgradeCity(String cityId);

    void endTurn();

    void quit();
}
