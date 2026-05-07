package pjvsemproj.models.services;

import pjvsemproj.dto.EntityDTO;
import pjvsemproj.dto.GameDTO;
import pjvsemproj.dto.PlayerDTO;
import pjvsemproj.dto.TileDTO;

import java.util.List;
import java.util.Set;

// TODO change action methods return type from void to boolean
/**
 * Defines the interface for interacting with the game.
 *
 * Acts as an abstraction layer between UI/network and game logic.
 */
public interface CoreGameService {

    boolean moveUnit(String unitId, int x, int y);

    boolean attack(String attackerId, String targetId);

    boolean buyUnit(String cityId, String troopType);
    boolean upgradeCity(String cityId);

    void endTurn();

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
