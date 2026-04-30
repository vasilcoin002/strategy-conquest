package pjvsemproj.models.services;

import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TileDTO;

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

    void moveUnit(String unitId, int x, int y);

    void attack(String attackerId, String targetId);

    void buyUnit(String cityId, String troopType);
    void upgradeCity(String cityId);

    void endTurn();

    void quit();

    GameDTO getGameDTO();
    EntityDTO getEntityDTO(String entityId);

    int getMapWidth();
    int getMapHeight();
    TileDTO getTileDTO(int x, int y);

    List<PlayerDTO> getPlayersDTO();
    PlayerDTO getCurrentPlayerDTO();

    Set<TileDTO> getAvailableTilesDTOForMovement(String unitId);
    Set<TileDTO> getAvailableTilesDTOForAttack(String unitId);
}
