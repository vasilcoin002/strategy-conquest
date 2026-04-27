package pjvsemproj.dto;

import java.util.List;

/**
 * A simple container holding the raw data for a game level.
 */
public class GameDTO {
    public final int mapWidth;
    public final int mapHeight;
    public final int startingGold;
    public final List<EntityDTO> entities;

    public GameDTO(int mapWidth, int mapHeight, int startingGold, List<EntityDTO> entities) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startingGold = startingGold;
        this.entities = entities;
    }
}