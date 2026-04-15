package pjvsemproj.models.services;

import pjvsemproj.models.entities.troopUnits.TroopUnit;
import pjvsemproj.models.game.maps.GameMap;
import pjvsemproj.models.game.maps.Tile;
import pjvsemproj.models.game.players.Player;

import java.util.List;
import java.util.Set;


/**
 * Defines the interface for interacting with the game.
 *
 * Acts as an abstraction layer between UI/network and game logic.
 */

public interface GameService {

    void login(String playerName);

    void ready();

    GameMap getMap();

    List<Player> getPlayers();

    Player getCurrentPlayer();

    Set<Tile> getAvailableTilesForMovement(String unitId);

    void moveTroopUnit(String unitId, int x, int y);

    void attack(String attackerId, String targetId);

    void buyUnit(String cityId, String troopType);

    void upgradeCity(String cityId);

    void endTurn();

    void quit();
}
