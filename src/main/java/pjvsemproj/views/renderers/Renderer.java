package pjvsemproj.views.renderers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pjvsemproj.models.entities.IGridEntity;
import pjvsemproj.models.game.maps.Tile;

import static pjvsemproj.views.ViewConstants.TILE_SIZE;

public abstract class Renderer {
    protected final Canvas canvas;
    protected final GraphicsContext gc;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGc() {
        return gc;
    }

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

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
