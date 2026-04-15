package pjvsemproj.config;

import java.util.List;

/**
 * A simple container holding the raw data for a game level.
 */
public class GameConfigDTO {
    public final int mapWidth;
    public final int mapHeight;
    public final int startingGold;
    public final List<EntityConfigDTO> entities;

    public GameConfigDTO(int mapWidth, int mapHeight, int startingGold, List<EntityConfigDTO> entities) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startingGold = startingGold;
        this.entities = entities;
    }
}