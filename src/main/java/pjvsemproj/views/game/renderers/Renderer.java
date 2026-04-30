package pjvsemproj.views.game.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pjvsemproj.dto.EntityDTO;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

/**
 * Base renderer class providing helper methods for drawing entities.
 */
public abstract class Renderer {

//    public Tile getTile(IGridEntity entity) {
//        return entity.getTile();
//    }

    public int getEntityGameX(EntityDTO entity) {
        return entity.x;
    }

    public int getEntityViewX(EntityDTO entity) {
        return getEntityGameX(entity) * TILE_SIZE;
    }

    public int getEntityGameY(EntityDTO entity) {
        return entity.y;
    }

    public int getEntityViewY(EntityDTO entity) {
        return getEntityGameY(entity) * TILE_SIZE;
    }

    public void clear(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        clear(gc, 0, 0, width, height);
    }

    public void clear(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        gc.clearRect(x1, y1, x2, y2);
    }


}
