package pjvsemproj.dto;

import java.util.List;

public class TileDTO {
    private final int x;
    private final int y;
    private final List<EntityDTO> entities;

    public TileDTO(int x, int y, List<EntityDTO> entities) {
        this.x = x;
        this.y = y;
        this.entities = entities;
    }
}
