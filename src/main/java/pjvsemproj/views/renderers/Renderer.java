package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public abstract class Renderer {

    public Tile getTile(IGridEntity entity) {
        return entity.getTile();
    }

    public int getEntityGameX(IGridEntity entity) {
        return getTile(entity).getX();
    }

    public int getEntityViewX(IGridEntity entity) {
        return getEntityGameX(entity) * TILE_SIZE;
    }

    public int getEntityGameY(IGridEntity entity) {
        return getTile(entity).getY();
    }

    public int getEntityViewY(IGridEntity entity) {
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
