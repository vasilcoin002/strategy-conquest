package pjvsemproj.dto;

import java.util.List;

/**
 * A simple container holding the raw data for a game level.
 */
public class GameDTO {
    public final int mapWidth;
    public final int mapHeight;
    public final List<EntityDTO> entities;
    public final List<PlayerDTO> players;
    public PlayerDTO currentPlayer;

    public GameDTO(int mapWidth, int mapHeight, List<EntityDTO> entities, List<PlayerDTO> players, PlayerDTO currentPlayer) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.entities = entities;
        this.players = players;
        this.currentPlayer = currentPlayer;
    }
}